package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
import ch.hslu.swda.entities.WarehouseEntity;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import jakarta.inject.Singleton;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the product catalog using MongoDB.
 */
@Singleton
public final class ProductCatalogDB implements ProductCatalog {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogDB.class);
    public static final String COLLECTION = "catalog";

    private final MongoDBConnector db;

    /**
     * Constructor with configuration from the environment variables.
     */
    public ProductCatalogDB() {
        this(new MongoDBConnector(COLLECTION));
    }

    /**
     * Constructor with custom configuration.
     */
    public ProductCatalogDB(final MongoDBConnector connector) {
        db = connector;
    }

    @Override
    public Article getById(long branchId, long articleId) {
        LOG.info("DB: read article from branch {} with id {}", branchId, articleId);
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        Document exists = this.db.collection().find(filter).first();
        return exists != null ? new Article(exists) : null;
    }

    @Override
    public Map<Long, Article> getById(long branchId, List<Long> articleIds) {
        LOG.info("DB: read articles from branch {} with ids {}", branchId, articleIds);
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.in("articleId", articleIds));
        List<Document> documents = this.db.collection().find(filter).into(new ArrayList<>());
        return documents.stream().map(Article::new).collect(Collectors.toMap(Article::articleId, a -> a));
    }

    @Override
    public List<Article> getAll(long branchId) {
        Bson filter = Filters.eq("branchId", branchId);
        List<Document> documents = this.db.collection().find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} articles from branch {}", documents.size(), branchId);
        return documents.stream().map(Article::new).toList();
    }

    @Override
    public Article create(long branchId, Article article) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", article.articleId()));
        Document exists = this.db.collection().find(filter).first();
        if (exists == null) {
            WarehouseEntity<Article> warehouseEntity = new WarehouseEntity<>(branchId, article);
            this.db.collection().insertOne(warehouseEntity.toDocument());
            LOG.info("DB: created article for branch {} with id {}", branchId, article.articleId());
        } else {
            LOG.warn("DB: article {} already exists for branch {}", article.articleId(), branchId);
        }
        return exists == null ? article : new Article(exists);
    }

    @Override
    public Article update(long branchId, long articleId, Article article) {
        if (articleId != article.articleId()) {
            article = new Article(articleId, article.name(), article.price(),
                    article.minStock(), article.stock(), article.reserved());
        }

        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        WarehouseEntity<Article> warehouseEntity = new WarehouseEntity<>(branchId, article);
        Document exists = this.db.collection().findOneAndReplace(filter, warehouseEntity.toDocument());
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
        Document removed = this.db.collection().findOneAndDelete(filter);
        LOG.info("DB: {}removed article from branch {} with id {}", removed != null ? "" : "not ", branchId, articleId);
        return removed != null;
    }

    @Override
    public boolean changeStock(long branchId, long articleId, int amount) {
        return incrementField("stock", branchId, articleId, amount);
    }

    @Override
    public boolean changeReserved(long branchId, long articleId, int amount) {
        return incrementField("reserved", branchId, articleId, amount);
    }

    @Override
    public List<WarehouseEntity<Article>> getLowStock() {
        String expression = "{ $lt: [ { $subtract: ['$stock', '$reserved'] }, '$minStock' ] }";
        Bson filter = Filters.expr(Document.parse(expression));
        List<Document> documents = this.db.collection().find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} articles with low stock", documents.size());
        return documents.stream().map(d -> new WarehouseEntity<>(d.getLong("branchId"), new Article(d))).toList();
    }

    /**
     * Increments the specified field by the given amount.
     *
     * @param field     Name of the field to increment.
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @param amount    Amount to increment.
     * @return True if successful, false if not.
     */
    private boolean incrementField(String field, long branchId, long articleId, int amount) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("articleId", articleId));
        if (amount < 0) {
            filter = Filters.and(filter, Filters.gte(field, Math.abs(amount)));
        }
        long result = this.db.collection().updateOne(filter, Updates.inc(field, amount)).getModifiedCount();
        LOG.info("DB: {}updated {} items of article from branch {} with id {}",
                result == 1 ? "" : "not ", field, branchId, articleId);
        return result == 1;
    }
}
