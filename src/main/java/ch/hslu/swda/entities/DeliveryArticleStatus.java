package ch.hslu.swda.entities;

/**
 * Defines the state a delivery article can be in.
 */
public enum DeliveryArticleStatus {

    /**
     * The article should be added to the delivery.
     */
    ADD,

    /**
     * The article should be modified in the delivery.
     */
    MODIFY,

    /**
     * The article should be removed from the delivery.
     */
    REMOVE,

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
