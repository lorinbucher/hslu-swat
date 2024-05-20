package ch.hslu.swda.business;

import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryStatus;
import ch.hslu.swda.entities.WarehouseEntity;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.lang.Nullable;
import jakarta.inject.Singleton;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the deliveries using MongoDB.
 */
@Singleton
public final class DeliveriesDB implements Deliveries {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveriesDB.class);
    public static final String COLLECTION = "deliveries";

    private static final String BRANCH_ID = "branchId";
    private static final String ORDER_NUMBER = "orderNumber";
    private static final String STATUS = "status";

    private final MongoDBConnector db;

    /**
     * Constructor with configuration from the environment variables.
     */
    public DeliveriesDB() {
        this(new MongoDBConnector(COLLECTION));
    }

    /**
     * Constructor with custom configuration.
     *
     * @param connector MongoDB connector.
     */
    public DeliveriesDB(final MongoDBConnector connector) {
        db = connector;
    }

    @Override
    public Delivery getById(final long branchId, final long orderNumber) {
        LOG.info("DB: read delivery from branch {} with id {}", branchId, orderNumber);
        Bson filter = Filters.and(Filters.eq(BRANCH_ID, branchId), Filters.eq(ORDER_NUMBER, orderNumber));
        Document exists = this.db.collection().find(filter).first();
        return exists != null ? new Delivery(exists) : null;
    }

    @Override
    public List<Delivery> getAllByBranch(final long branchId, @Nullable final DeliveryStatus status) {
        Bson filter = Filters.eq(BRANCH_ID, branchId);
        if (status != null) {
            filter = Filters.and(filter, Filters.eq(STATUS, status.name()));
        }
        List<Document> documents = this.db.collection().find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} deliveries from branch {}{}", documents.size(), branchId,
                status != null ? " with status " + status : "");
        return documents.stream().map(Delivery::new).toList();
    }

    @Override
    public List<WarehouseEntity<Delivery>> getAllByStatus(final DeliveryStatus status) {
        Bson filter = Filters.eq(STATUS, status);
        List<Document> documents = this.db.collection().find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} deliveries with status {}", documents.size(), status);
        return documents.stream().map(d -> new WarehouseEntity<>(d.getLong(BRANCH_ID), new Delivery(d))).toList();
    }

    @Override
    public Delivery create(final long branchId, final Delivery delivery) {
        Bson filter = Filters.and(Filters.eq(BRANCH_ID, branchId), Filters.eq(ORDER_NUMBER, delivery.orderNumber()));
        Document exists = this.db.collection().find(filter).first();
        if (exists == null) {
            WarehouseEntity<Delivery> warehouseEntity = new WarehouseEntity<>(branchId, delivery);
            this.db.collection().insertOne(warehouseEntity.toDocument());
            LOG.info("DB: created delivery for branch {} with id {}", branchId, delivery.orderNumber());
        } else {
            LOG.warn("DB: delivery {} already exists for branch {}", delivery.orderNumber(), branchId);
        }
        return exists == null ? delivery : new Delivery(exists);
    }

    @Override
    public Delivery update(final long branchId, final long orderNumber, final Delivery delivery) {
        Delivery current = delivery;
        if (orderNumber != delivery.orderNumber()) {
            current = new Delivery(orderNumber, delivery.status(), delivery.articles());
        }

        Bson filter = Filters.and(Filters.eq(BRANCH_ID, branchId), Filters.eq(ORDER_NUMBER, orderNumber));
        WarehouseEntity<Delivery> warehouseEntity = new WarehouseEntity<>(branchId, current);
        FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        Document updated = this.db.collection().findOneAndReplace(filter, warehouseEntity.toDocument(), options);
        LOG.info("DB: {}updated delivery for branch {} with id {}",
                updated != null ? "" : "not ", branchId, orderNumber);
        return updated != null ? new Delivery(updated) : null;
    }

    @Override
    public Delivery updateStatus(final long branchId, final long orderNumber, final DeliveryStatus status) {
        Bson filter = Filters.and(Filters.eq(BRANCH_ID, branchId), Filters.eq(ORDER_NUMBER, orderNumber));
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        Document updated = this.db.collection().findOneAndUpdate(filter, Updates.set(STATUS, status), options);
        LOG.info("DB: {}updated delivery status for branch {} with id {} to {}",
                updated != null ? "" : "not ", branchId, orderNumber, status);
        return updated != null ? new Delivery(updated) : null;
    }

    @Override
    public boolean delete(final long branchId, final long orderNumber) {
        Bson filter = Filters.and(Filters.eq(BRANCH_ID, branchId), Filters.eq(ORDER_NUMBER, orderNumber));
        Document removed = this.db.collection().findOneAndDelete(filter);
        LOG.info("DB: {}removed delivery from branch {} with id {}",
                removed != null ? "" : "not ", branchId, orderNumber);
        return removed != null;
    }
}
