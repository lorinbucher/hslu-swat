package ch.hslu.swda.entities;

import org.bson.Document;

/**
 * An entity in the warehouse.
 */
public record WarehouseEntity<T>(long branchId, Entity<T> entity) {

    public WarehouseEntity {
        if (branchId < 1) {
            throw new IllegalArgumentException("branchId should not be lower than 1");
        }
    }

    /**
     * Creates a MongoDB document from a warehouse entity.
     *
     * @return MongoDB document.
     */
    public Document toDocument() {
        Document document = new Document("branchId", branchId);
        document.putAll(entity.toDocument());
        return document;
    }
}
