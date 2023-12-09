package ch.hslu.swda.business;

import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
import ch.hslu.swda.entities.WarehouseEntity;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.lang.Nullable;
import jakarta.inject.Singleton;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the reorders using MongoDB.
 */
@Singleton
public class ReordersDB implements Reorders {

    private static final Logger LOG = LoggerFactory.getLogger(Reorders.class);
    private static final String DATABASE = "warehouse";
    private static final String COLLECTION = "reorders";
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    /**
     * Constructor using environment variables for db configuration.
     */
    public ReordersDB() {
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
    public ReordersDB(final String host, final String user, final String password) {
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
    public Reorder getById(long branchId, long reorderId) {
        LOG.info("DB: read reorder from branch {} with id {}", branchId, reorderId);
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("reorderId", reorderId));
        Document exists = this.collection.find(filter).first();
        return exists != null ? new Reorder(exists) : null;
    }

    @Override
    public List<Reorder> getAll(long branchId, @Nullable ReorderStatus status) {
        Bson filter = Filters.eq("branchId", branchId);
        if (status != null) {
            filter = Filters.and(filter, Filters.eq("status", status.name()));
        }
        List<Document> documents = this.collection.find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} reorders from branch {}{}", documents.size(), branchId,
                status != null ? " with status " + status : "");
        return documents.stream().map(Reorder::new).toList();
    }

    @Override
    public Reorder create(long branchId, long articleId, int quantity) {
        Document lastDocument = this.collection.find().sort(Sorts.descending("reorderId")).limit(1).first();
        long newReorderId = 1L;
        if (lastDocument != null) {
            newReorderId = new Reorder(lastDocument).reorderId() + 1;
        }

        Reorder reorder = new Reorder(newReorderId, ReorderStatus.NEW, "", articleId, quantity);
        WarehouseEntity<Reorder> warehouseEntity = new WarehouseEntity<>(branchId, reorder);
        this.collection.insertOne(warehouseEntity.toDocument());
        LOG.info("DB: created reorder for branch {} with id {}", branchId, reorder.reorderId());
        return reorder;
    }

    @Override
    public Reorder updateStatus(long branchId, long reorderId, ReorderStatus status) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("reorderId", reorderId));
        Document updated = null;
        Document exists = this.collection.find(filter).first();
        // TODO: use findOneAndUpdate function to make it atomic
        if (exists != null) {
            Reorder reorder = new Reorder(exists);
            reorder = new Reorder(reorder.reorderId(), status, reorder.date(), reorder.articleId(), reorder.quantity());
            WarehouseEntity<Reorder> warehouseEntity = new WarehouseEntity<>(branchId, reorder);
            FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
            updated = this.collection.findOneAndReplace(filter, warehouseEntity.toDocument(), options);
        }

        LOG.info("DB: {}updated reorder status for branch {} with id {} to {}",
                updated != null ? "" : "not ", branchId, reorderId, status);
        return updated != null ? new Reorder(updated) : null;
    }

    @Override
    public boolean delete(long branchId, long reorderID) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("reorderId", reorderID));
        Document removed = this.collection.findOneAndDelete(filter);
        LOG.info("DB: {}removed delivery from branch {} with id {}", removed != null ? "" : "not ", branchId, reorderID);
        return removed != null;
    }
}
