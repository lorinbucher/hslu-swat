package ch.hslu.swda.business;

import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryStatus;
import ch.hslu.swda.entities.WarehouseDelivery;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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
public class DeliveriesDB implements Deliveries {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogDB.class);
    private static final String DATABASE = "warehouse";
    private static final String COLLECTION = "deliveries";
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    /**
     * Constructor using environment variables for db configuration.
     */
    public DeliveriesDB() {
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
    public DeliveriesDB(final String host, final String user, final String password) {
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
    public Delivery getById(long branchId, long orderNumber) {
        LOG.info("DB: read delivery from branch {} with id {}", branchId, orderNumber);
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("orderNumber", orderNumber));
        Document exists = this.collection.find(filter).first();
        return exists != null ? WarehouseDelivery.fromDocument(exists).delivery() : null;
    }

    @Override
    public List<Delivery> getAll(long branchId, @Nullable DeliveryStatus status) {
        Bson filter = Filters.eq("branchId", branchId);
        if (status != null) {
            filter = Filters.and(filter, Filters.eq("status", status.name()));
        }
        List<Document> documents = this.collection.find(filter).into(new ArrayList<>());
        LOG.info("DB: read all {} deliveries from branch {}{}", documents.size(), branchId,
                status != null ? " with status " + status : "");
        return documents.stream().map(WarehouseDelivery::fromDocument).map(WarehouseDelivery::delivery).toList();
    }

    @Override
    public Delivery create(long branchId, Delivery delivery) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("orderNumber", delivery.orderNumber()));
        Document exists = this.collection.find(filter).first();
        if (exists == null) {
            Document doc = WarehouseDelivery.toDocument(new WarehouseDelivery(branchId, delivery));
            this.collection.insertOne(doc);
            LOG.info("DB: created delivery for branch {} with id {}", branchId, delivery.orderNumber());
        } else {
            LOG.warn("DB: delivery {} already exists for branch {}", delivery.orderNumber(), branchId);
        }
        return exists == null ? delivery : WarehouseDelivery.fromDocument(exists).delivery();
    }

    @Override
    public Delivery update(long branchId, long orderNumber, Delivery delivery) {
        if (orderNumber != delivery.orderNumber()) {
            delivery = new Delivery(orderNumber, delivery.status(), delivery.articles());
        }

        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("orderNumber", orderNumber));
        Document deliveryDocument = WarehouseDelivery.toDocument(new WarehouseDelivery(branchId, delivery));
        Document exists = this.collection.findOneAndReplace(filter, deliveryDocument);
        Delivery updated;
        if (exists != null) {
            updated = delivery;
            LOG.info("DB: updated delivery from branch {} with id {}", branchId, orderNumber);
        } else {
            updated = create(branchId, delivery);
        }
        return updated;
    }

    @Override
    public boolean delete(long branchId, long orderNumber) {
        Bson filter = Filters.and(Filters.eq("branchId", branchId), Filters.eq("orderNumber", orderNumber));
        Document removed = this.collection.findOneAndDelete(filter);
        LOG.info("DB: {}removed delivery from branch {} with id {}", removed != null ? "" : "not ", branchId, orderNumber);
        return removed != null;
    }
}
