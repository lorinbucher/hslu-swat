package ch.hslu.swda.micronaut;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.dto.ArticleDeliveredDTO;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryStatus;
import ch.hslu.swda.micro.DeliveryMessageHandler;
import ch.hslu.swda.micro.DeliveryProcessor;
import ch.hslu.swda.micro.EventLogger;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.*;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
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

    @Inject
    private DeliveryMessageHandler deliveryMessageHandler;

    @Inject
    private DeliveryProcessor deliveryProcessor;

    @Inject
    private EventLogger eventLogger;

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


    /**
     * Change status of the delivery for the specified order number of the branch.
     * Only supports changing the status to `COMPLETED`, the other status are handled by the system.
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @param status      Updated delivery status.
     * @return Delivery.
     */
    @Tag(name = "delivery")
    @Patch("/{branchId}/{orderNumber}")
    public Delivery changeState(final long branchId, final long orderNumber, @JsonProperty DeliveryStatus status) {
        if (status != DeliveryStatus.COMPLETED) {
            LOG.warn("REST: Delivery status cannot be changed to {}", status);
            throw new IllegalArgumentException("Delivery status can only be changed to COMPLETED");
        }

        Delivery delivery = deliveryProcessor.changeToCompleted(branchId, orderNumber);
        if (delivery != null) {
            LOG.info("REST: Delivery {} from branch {} completed: {}", orderNumber, branchId, delivery);
            deliveryMessageHandler.publishDelivered(new ArticleDeliveredDTO(branchId, orderNumber));
            String message = "Delivered articles for order number " + orderNumber;
            eventLogger.publishLog(new LogEventDTO(branchId, "delivery.completed", message));
        } else {
            LOG.error("REST: Delivery {} from branch {} not completed", orderNumber, branchId);
        }
        return delivery;
    }

    @Error(exception = IllegalArgumentException.class, global = true)
    public HttpResponse<JsonError> invalidStatus(HttpRequest request, IllegalArgumentException ex) {
        JsonError error = new JsonError(ex.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.<JsonError>badRequest().body(error);
    }

    @Error(exception = IllegalStateException.class, global = true)
    public HttpResponse<JsonError> notReady(HttpRequest request, IllegalStateException ex) {
        JsonError error = new JsonError(ex.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.<JsonError>badRequest().body(error);
    }
}
