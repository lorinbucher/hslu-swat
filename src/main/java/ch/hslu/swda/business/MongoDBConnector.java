package ch.hslu.swda.business;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * MongoDB connector for the warehouse.
 */
public final class MongoDBConnector {

    private static final String DATABASE_NAME = "warehouse";

    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    /**
     * Constructor using environment variables for db configuration.
     *
     * @param collection MongoDB collection.
     */
    public MongoDBConnector(final String collection) {
        this(
                collection,
                System.getenv().getOrDefault("MONGO_HOST", "localhost"),
                System.getenv().getOrDefault("MONGO_USER", ""),
                System.getenv().getOrDefault("MONGO_PASSWORD", "")
        );
    }

    /**
     * Constructor with arguments for db configuration.
     *
     * @param collection MongoDB collection.
     * @param host       MongoDB host.
     * @param user       MongoDB user.
     * @param password   MongoDB password.
     */
    public MongoDBConnector(final String collection, final String host, final String user, final String password) {
        String connectionURI = "mongodb://";
        if (!user.isBlank() && !password.isBlank()) {
            connectionURI += String.format("%s:%s@%s", user, password, host);
        } else {
            connectionURI += host;
        }

        this.client = MongoClients.create(connectionURI);
        this.database = this.client.getDatabase(DATABASE_NAME);
        this.collection = this.database.getCollection(collection);
    }

    /**
     * Returns the MongoDB collection.
     *
     * @return MongoDB collection.
     */
    public MongoCollection<Document> collection() {
        return collection;
    }
}
