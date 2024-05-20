package ch.hslu.swda.business;

import ch.hslu.swda.entities.DeliveryStatus;
import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
import ch.hslu.swda.entities.WarehouseEntity;
import com.mongodb.client.model.*;
import com.mongodb.lang.Nullable;
import jakarta.inject.Singleton;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the reorders using MongoDB.
 */
@Singleton
public final class ReordersDB implements Reorders {

    private static final Logger LOG = LoggerFactory.getLogger(ReordersDB.class);
    public static final String COLLECTION = "reorders";

    private final MongoDBConnector db;

    /**
     * Constructor with configuration from the environment variables.
     */
    public ReordersDB() {
        this(new MongoDBConnector(COLLECTION));
    }

    /**
     * Constructor with custom configuration.
     */
    public ReordersDB(final MongoDBConnector connector) {
        db = connector;
    }

    @Override
    public Reorder getById(long branchId, long reorderId) {
        LOG.info("DB: read reorder from branch {} with id {}", branchId, reorderId);
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("reorderId", reorderId));
        Document exists = this.db.collection().find(filter).first();
        return exists != null ? new Reorder(exists) : null;
    }

    @Override
    public List<Reorder> getAllByBranch(long branchId, @Nullable ReorderStatus status) {
        Bson filter = Filters.eq("branchId", branchId);
        if (status != null) {
            filter = Filters.and(filter, Filters.eq("status", status.name()));
        }
        List<Document> documents = this.db.collection().find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} reorders from branch {}{}", documents.size(), branchId,
                status != null ? " with status " + status : "");
        return documents.stream().map(Reorder::new).toList();
    }

    @Override
    public List<WarehouseEntity<Reorder>> getAllByStatus(ReorderStatus status) {
        Bson filter = Filters.eq("status", status);
        List<Document> documents = this.db.collection().find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} reorders with status {}", documents.size(), status);
        return documents.stream().map(d -> new WarehouseEntity<>(d.getLong("branchId"), new Reorder(d))).toList();
    }

    @Override
    public Reorder create(long branchId, long articleId, int quantity) {
        Document lastDocument = this.db.collection().find().sort(Sorts.descending("reorderId")).limit(1).first();
        long newReorderId = 1L;
        if (lastDocument != null) {
            newReorderId = new Reorder(lastDocument).reorderId() + 1;
        }

        Reorder reorder = new Reorder(newReorderId, ReorderStatus.NEW, "", articleId, quantity);
        WarehouseEntity<Reorder> warehouseEntity = new WarehouseEntity<>(branchId, reorder);
        this.db.collection().insertOne(warehouseEntity.toDocument());
        LOG.info("DB: created reorder for branch {} with id {}", branchId, reorder.reorderId());
        return reorder;
    }

    @Override
    public Reorder updateStatus(long branchId, long reorderId, ReorderStatus status) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("reorderId", reorderId));
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        Document updated = this.db.collection().findOneAndUpdate(filter, Updates.set("status", status), options);
        LOG.info("DB: {}updated reorder status for branch {} with id {} to {}",
                updated != null ? "" : "not ", branchId, reorderId, status);
        return updated != null ? new Reorder(updated) : null;
    }

    @Override
    public Reorder updateQuantity(long branchId, long reorderId, int quantity) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("reorderId", reorderId));
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        Document updated = this.db.collection().findOneAndUpdate(filter, Updates.set("quantity", quantity), options);
        LOG.info("DB: {}updated reorder quantity for branch {} with id {} to {}",
                updated != null ? "" : "not ", branchId, reorderId, quantity);
        return updated != null ? new Reorder(updated) : null;
    }

    @Override
    public boolean delete(long branchId, long reorderID) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("reorderId", reorderID));
        Document removed = this.db.collection().findOneAndDelete(filter);
        LOG.info("DB: {}removed delivery from branch {} with id {}",
                removed != null ? "" : "not ", branchId, reorderID);
        return removed != null;
    }

    @Override
    public int countReorderedArticles(long branchId, long articleId) {
        Bson match = Aggregates.match(Filters.and(
                Filters.eq("branchId", branchId),
                Filters.eq("articleId", articleId),
                Filters.ne("status", DeliveryStatus.COMPLETED.name()))
        );
        Bson group = Aggregates.group(null, Accumulators.sum("count", "$quantity"));
        Document document = this.db.collection().aggregate(Arrays.asList(match, group)).first();

        int count = 0;
        if (document != null) {
            count = document.getInteger("count");
        }
        LOG.info("DB: number of reordered articles from branch {} with id {}: {}", branchId, articleId, count);
        return count;
    }
}
