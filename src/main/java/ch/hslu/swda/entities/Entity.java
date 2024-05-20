package ch.hslu.swda.entities;

import org.bson.Document;

/**
 * An entity in the warehouse.
 *
 * @param <T> The type of the entity in the warehouse.
 */
public interface Entity<T> {

    /**
     * Creates a MongoDB document from a warehouse entity.
     */
    Document toDocument();

}
