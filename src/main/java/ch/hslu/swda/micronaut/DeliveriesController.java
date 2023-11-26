package ch.hslu.swda.micronaut;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryStatus;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for the deliveries.
 */
@Controller("/api/v1/delivery")
public final class DeliveriesController {
    private static final Logger LOG = LoggerFactory.getLogger(DeliveriesController.class);

    @Inject
    private Deliveries deliveries;

    /**
     * Get all deliveries of the branch.
     *
     * @param branchId ID of the branch.
     * @param status   Delivery status filter.
     * @return List of all deliveries.
     */
    @Tag(name = "delivery")
    @Get("/{branchId}")
    public List<Delivery> getAll(final long branchId, @QueryValue("status") @Nullable final DeliveryStatus status) {
        final List<Delivery> result = deliveries.getAll(branchId, status);
        LOG.info("REST: All {} deliveries from branch {}{} returned.", result.size(), branchId,
                status != null ? " with status " + status : "");
        return result;
    }

    /**
     * Get delivery with the specified order number of the branch.
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @return Delivery.
     */
    @Tag(name = "delivery")
    @Get("/{branchId}/{orderNumber}")
    public Delivery get(final long branchId, final long orderNumber) {
        final Delivery delivery = deliveries.getById(branchId, orderNumber);
        LOG.info("REST: Delivery {} from branch {} {}.", orderNumber, branchId, delivery != null ? "returned" : "not found");
        return delivery;
    }
}
