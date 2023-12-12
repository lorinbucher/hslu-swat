package ch.hslu.swda.business;

import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryStatus;
import com.mongodb.lang.Nullable;

import java.util.List;

/**
 * Management of the order deliveries.
 */
public interface Deliveries {

    /**
     * Returns the delivery for the specified order number of the branch.
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @return Delivery.
     */
    Delivery getById(long branchId, long orderNumber);

    /**
     * Returns all deliveries of a branch.
     *
     * @param branchId ID of the branch.
     * @param status   Optional status filter.
     * @return List of all deliveries from a branch.
     */
    List<Delivery> getAllByBranch(long branchId, @Nullable DeliveryStatus status);

    /**
     * Returns all deliveries.
     *
     * @param status Status of the deliveries.
     * @return List of all deliveries.
     */
    List<Delivery> getAllByStatus(DeliveryStatus status);

    /**
     * Adds a delivery for the branch.
     *
     * @param branchId ID of the branch.
     * @param delivery Delivery.
     * @return Delivery.
     */
    Delivery create(long branchId, Delivery delivery);

    /**
     * Updates a delivery of the branch.
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @param delivery    Delivery.
     * @return Delivery.
     */
    Delivery update(long branchId, long orderNumber, Delivery delivery);

    /**
     * Updates the status of a delivery of the branch.
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @param status      Delivery status.
     * @return Delivery.
     */
    Delivery updateStatus(long branchId, long orderNumber, DeliveryStatus status);

    /**
     * Deletes a delivery from the branch.
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @return True if successful, false if not.
     */
    boolean delete(long branchId, long orderNumber);
}
