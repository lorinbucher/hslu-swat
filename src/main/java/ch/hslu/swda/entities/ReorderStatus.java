package ch.hslu.swda.entities;

/**
 * Defines the state a reorder can be in.
 */
public enum ReorderStatus {

    /**
     * A new reorder has been registered.
     */
    NEW,

    /**
     * Waiting on delivery from the central warehouse.
     */
    WAITING,

    /**
     * The reorder was delivered from the central warehouse.
     */
    DELIVERED,

    /**
     * The reorder process has been completed.
     */
    COMPLETED
}
