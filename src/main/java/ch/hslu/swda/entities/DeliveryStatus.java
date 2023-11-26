package ch.hslu.swda.entities;

/**
 * Defines the state a delivery can be in.
 */
public enum DeliveryStatus {

    /**
     * A new delivery has been registered by an incoming order.
     */
    NEW,

    /**
     * The delivery has been modified because the order changed.
     */
    MODIFIED,

    /**
     * Some articles are not in stock, waiting on delivery from the central warehouse.
     */
    WAITING,

    /**
     * All articles are in stock and reserved, waiting for delivery.
     */
    READY,

    /**
     * All articles of the order were delivered.
     */
    COMPLETED
}
