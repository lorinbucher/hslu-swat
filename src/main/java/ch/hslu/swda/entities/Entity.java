package ch.hslu.swda.entities;

import org.bson.Document;

/**
 * An entity in the warehouse.
 */
public interface Entity<T> {

    /**
     * Creates a MongoDB document from a warehouse entity.
     */
    public Document toDocument();

}
