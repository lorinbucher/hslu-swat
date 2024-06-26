package ch.hslu.swda.micronaut;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.dto.ArticleDeliveredDTO;
import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryStatus;
import ch.hslu.swda.micro.MessagePublisher;
import ch.hslu.swda.micro.Routes;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.QueryValue;
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

    private final Deliveries deliveries;
    private final MessagePublisher<ArticleDeliveredDTO> deliveryPublisher;

    @Inject
    public DeliveriesController(final Deliveries deliveries, final MessagePublisher<ArticleDeliveredDTO> publisher) {
        this.deliveries = deliveries;
        this.deliveryPublisher = publisher;
    }

    /**
     * Get all deliveries of the branch.
     * <p>
     * Roles: Branch Manager
     *
     * @param branchId ID of the branch.
     * @param status   Delivery status filter.
     * @return List of all deliveries.
     */
    @Tag(name = "delivery")
    @Get("/{branchId}")
    public List<Delivery> getAll(final long branchId, @QueryValue("status") @Nullable final DeliveryStatus status) {
        final List<Delivery> result = deliveries.getAllByBranch(branchId, status);
        LOG.info("REST: All {} deliveries from branch {}{} returned.",
                result.size(), branchId, status != null ? " with status " + status : "");
        return result;
    }

    /**
     * Get delivery with the specified order number of the branch.
     * <p>
     * Roles: Branch Manager
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @return Delivery.
     */
    @Tag(name = "delivery")
    @Get("/{branchId}/{orderNumber}")
    public Delivery get(final long branchId, final long orderNumber) {
        final Delivery delivery = deliveries.getById(branchId, orderNumber);
        LOG.info("REST: Delivery {} from branch {} {}.",
                orderNumber, branchId, delivery != null ? "returned" : "not found");
        return delivery;
    }

    /**
     * Change status of the delivery for the specified order number of the branch.
     * Only supports changing the status to `DELIVERED`, the other status are handled by the system.
     * <p>
     * Roles: Data Typist
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @param status      Updated delivery status.
     * @return Delivery.
     */
    @Tag(name = "delivery")
    @Patch("/{branchId}/{orderNumber}")
    public Delivery changeStatus(final long branchId, final long orderNumber,
                                 @JsonProperty final DeliveryStatus status) {
        if (status != DeliveryStatus.DELIVERED) {
            LOG.warn("REST: Delivery status cannot be changed to {}", status);
            throw new IllegalArgumentException("Delivery status can only be changed to DELIVERED");
        }

        Delivery delivery = deliveries.updateStatus(branchId, orderNumber, status);
        if (delivery != null) {
            LOG.info("REST: Delivery {} from branch {} was delivered", orderNumber, branchId);
            deliveryPublisher.sendMessage(Routes.ARTICLE_DELIVERED, new ArticleDeliveredDTO(branchId, orderNumber));
        } else {
            LOG.error("REST: Failed to set status of delivery {} from branch {} to delivered", orderNumber, branchId);
        }
        return delivery;
    }

    /**
     * Handles the raised illegal argument exception when the status in the request payload is invalid.
     *
     * @param request Request that was processed when the exception occurred.
     * @param ex      Exception that occurred during the processing of the request.
     * @return Bad request HTTP response with the error reason.
     */
    @Error(exception = IllegalArgumentException.class)
    public HttpResponse<JsonError> invalidStatus(final HttpRequest<?> request, final IllegalArgumentException ex) {
        JsonError error = new JsonError(ex.getMessage()).link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.<JsonError>badRequest().body(error);
    }
}
