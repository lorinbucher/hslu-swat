package ch.hslu.swda.entities;

/**
 * Defines the state a delivery article can be in.
 */
public enum DeliveryArticleStatus {

    /**
     * The article is processed by the warehouse.
     */
    PROCESSING,

    /**
     * The article is reserved in the warehouse.
     */
    RESERVED,

    /**
     * The article is awaiting a delivery from the central warehouse.
     */
    ORDERED,

    /**
     * The article was delivered.
     */
    DELIVERED
}
