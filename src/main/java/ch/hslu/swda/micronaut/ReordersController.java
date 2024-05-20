package ch.hslu.swda.micronaut;

import ch.hslu.swda.business.Reorders;
import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
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
 * Controller for the reorders.
 */
@Controller("/api/v1/reorder")
public final class ReordersController {
    private static final Logger LOG = LoggerFactory.getLogger(ReordersController.class);

    private final Reorders reorders;

    @Inject
    public ReordersController(Reorders reorders) {
        this.reorders = reorders;
    }

    /**
     * Get all reorders of the branch.
     * <p>
     * Roles: Branch Manager
     *
     * @param branchId ID of the branch.
     * @param status   Status filter of the reorders.
     * @return List of all reorders.
     */
    @Tag(name = "reorder")
    @Get("/{branchId}")
    public List<Reorder> getAll(final long branchId, @QueryValue("status") @Nullable final ReorderStatus status) {
        final List<Reorder> result = reorders.getAllByBranch(branchId, status);
        LOG.info("REST: All {} reorders from branch {}{} returned.", result.size(), branchId,
                status != null ? " with status " + status : "");
        return result;
    }

    /**
     * Get reorder with the specified reorder ID of the branch.
     * <p>
     * Roles: Branch Manager
     *
     * @param branchId  ID of the branch.
     * @param reorderId ID of the reorder.
     * @return Reorder.
     */
    @Tag(name = "reorder")
    @Get("/{branchId}/{reorderId}")
    public Reorder get(final long branchId, final long reorderId) {
        final Reorder reorder = reorders.getById(branchId, reorderId);
        LOG.info("REST: Reorder {} from branch {} {}.", reorderId, branchId, reorder != null ? "returned" : "not found");
        return reorder;
    }

    /**
     * Change status of the reorder for the specified reorder ID of the branch.
     * Only supports changing the status to `DELIVERED`, the other status are handled by the system.
     * <p>
     * Roles: Data Typist
     *
     * @param branchId  ID of the branch.
     * @param reorderId ID of the reorder.
     * @param status    Updated reorder status.
     * @return Reorder.
     */
    @Tag(name = "reorder")
    @Patch("/{branchId}/{reorderId}")
    public Reorder changeStatus(final long branchId, final long reorderId, @JsonProperty ReorderStatus status) {
        if (status != ReorderStatus.DELIVERED) {
            LOG.warn("REST: Reorder status cannot be changed to {}", status);
            throw new IllegalArgumentException("Reorder status can only be changed to DELIVERED");
        }

        Reorder reorder = reorders.updateStatus(branchId, reorderId, status);
        if (reorder != null) {
            LOG.info("REST: Reorder {} from branch {} was delivered", reorderId, branchId);
        } else {
            LOG.error("REST: Failed to set status of reorder {} from branch {} to delivered", reorderId, branchId);
        }
        return reorder;
    }

    /**
     * Handles the raised illegal argument exception when the status in the request payload is invalid.
     *
     * @param request Request that was processed when the exception occurred.
     * @param ex      Exception that occurred during the processing of the request.
     * @return Bad request HTTP response with the error reason.
     */
    @Error(exception = IllegalArgumentException.class)
    public HttpResponse<JsonError> invalidStatus(HttpRequest<?> request, IllegalArgumentException ex) {
        JsonError error = new JsonError(ex.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.<JsonError>badRequest().body(error);
    }
}
