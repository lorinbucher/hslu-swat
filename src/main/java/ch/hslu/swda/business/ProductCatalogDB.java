package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
import ch.hslu.swda.entities.WarehouseEntity;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import jakarta.inject.Singleton;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the product catalog using MongoDB.
 */
@Singleton
public final class ProductCatalogDB implements ProductCatalog {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogDB.class);
    private static final String DATABASE = "warehouse";
    private static final String COLLECTION = "catalog";
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    /**
     * Constructor using environment variables for db configuration.
     */
    public ProductCatalogDB() {
        this(
                System.getenv().getOrDefault("MONGO_HOST", "localhost"),
                System.getenv().getOrDefault("MONGO_USER", ""),
                System.getenv().getOrDefault("MONGO_PASSWORD", "")
        );
    }

    /**
     * Constructor with arguments for db configuration.
     *
     * @param host     MongoDB host.
     * @param user     MongoDB user.
     * @param password MongoDB password.
     */
    public ProductCatalogDB(final String host, final String user, final String password) {
        String connectionURI = "mongodb://";
        if (!user.isBlank() && !password.isBlank()) {
            connectionURI += String.format("%s:%s@%s", user, password, host);
        } else {
            connectionURI += host;
        }

        this.client = MongoClients.create(connectionURI);
        this.database = this.client.getDatabase(DATABASE);
        this.collection = this.database.getCollection(COLLECTION);
    }

    @Override
    public Article getById(long branchId, long articleId) {
        LOG.info("DB: read article from branch {} with id {}", branchId, articleId);
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        Document exists = this.collection.find(filter).first();
        return exists != null ? new Article(exists) : null;
    }

    @Override
    public List<Article> getAll(long branchId) {
        Bson filter = Filters.eq("branchId", branchId);
        List<Document> documents = this.collection.find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} articles from branch {}", documents.size(), branchId);
        return documents.stream().map(Article::new).toList();
    }

    @Override
    public Article create(long branchId, Article article) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", article.articleId()));
        Document exists = this.collection.find(filter).first();
        if (exists == null) {
            WarehouseEntity<Article> warehouseEntity = new WarehouseEntity<>(branchId, article);
            this.collection.insertOne(warehouseEntity.toDocument());
            LOG.info("DB: created article for branch {} with id {}", branchId, article.articleId());
        } else {
            LOG.warn("DB: article {} already exists for branch {}", article.articleId(), branchId);
        }
        return exists == null ? article : new Article(exists);
    }

    @Override
    public Article update(long branchId, long articleId, Article article) {
        if (articleId != article.articleId()) {
            article = new Article(articleId, article.name(), article.price(), article.minStock(), article.stock());
        }

        // TODO: use findOneAndUpdate without updating stock to make it atomic
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        WarehouseEntity<Article> warehouseEntity = new WarehouseEntity<>(branchId, article);
        Document exists = this.collection.findOneAndReplace(filter, warehouseEntity.toDocument());
        Article updated;
        if (exists != null) {
            updated = article;
            LOG.info("DB: updated article from branch {} with id {}", branchId, articleId);
        } else {
            updated = create(branchId, article);
        }
        return updated;
    }

    @Override
    public boolean delete(long branchId, long articleId) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        Document removed = this.collection.findOneAndDelete(filter);
        LOG.info("DB: {}removed article from branch {} with id {}", removed != null ? "" : "not ", branchId, articleId);
        return removed != null;
    }

    @Override
    public boolean changeStock(long branchId, long articleId, int amount) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        Article article = this.collection.find(filter).map(Article::new).first();

        // TODO: use findOneAndUpdate and inc function with condition enough in stock to make it atomic
        boolean result = false;
        if (article != null) {
            int newStock = article.stock() + amount;
            if (newStock >= 0) {
                Article changed = new Article(articleId, article.name(), article.price(), article.minStock(), newStock);
                WarehouseEntity<Article> warehouseEntity = new WarehouseEntity<>(branchId, changed);
                result = this.collection.findOneAndReplace(filter, warehouseEntity.toDocument()) != null;
                LOG.info("DB: updated stock of article from branch {} with id {} -> {}", branchId, articleId, newStock);
            } else {
                LOG.info("DB: article from branch {} with id {} has not enough in stock", branchId, articleId);
            }
        } else {
            LOG.warn("DB: article from branch {} with id {} doesn't exist", branchId, articleId);
        }
        return result;
    }
}
