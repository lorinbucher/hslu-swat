package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
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
     * Constructor.
     */
    public ProductCatalogDB() {
        String host = System.getProperty("MONGO_HOST", "localhost");
        this.client = MongoClients.create("mongodb://" + host);
        this.database = this.client.getDatabase(DATABASE);
        this.collection = this.database.getCollection(COLLECTION);
    }

    @Override
    public Article getById(long branchId, long articleId) {
        LOG.info("DB: read article from branch {} with id {}", branchId, articleId);
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        Document exists = this.collection.find(filter).first();
        return exists != null ? Article.fromDocument(exists) : null;
    }

    @Override
    public List<Article> getAll(long branchId) {
        Bson filter = Filters.eq("branchId", branchId);
        List<Document> documents = this.collection.find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} articles from branch {}", documents.size(), branchId);
        return documents.stream().map(Article::fromDocument).toList();
    }

    @Override
    public Article create(long branchId, Article article) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", article.articleId()));
        Document exists = this.collection.find(filter).first();
        if (exists == null) {
            Document doc = Article.toDocument(article).append("branchId", branchId);
            this.collection.insertOne(doc);
            LOG.info("DB: created article for branch {} with id {}", branchId, article.articleId());
        } else {
            LOG.warn("DB: article {} already exists for branch {}", article.articleId(), branchId);
        }
        return exists == null ? article : Article.fromDocument(exists);
    }

    @Override
    public Article update(long branchId, long articleId, Article article) {
        if (articleId != article.articleId()) {
            article = new Article(articleId, article.name(), article.price(), article.minStock(), article.stock());
        }

        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        Document exists = this.collection.findOneAndReplace(filter, Article.toDocument(article).append("branchId", branchId));
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
}
