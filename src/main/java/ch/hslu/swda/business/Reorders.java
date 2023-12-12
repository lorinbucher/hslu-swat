package ch.hslu.swda.business;

import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
import ch.hslu.swda.entities.WarehouseEntity;
import com.mongodb.lang.Nullable;

import java.util.List;

/**
 * Management of the reorders.
 */
public interface Reorders {

    /**
     * Returns the reorder for the specified reorder id of the branch.
     *
     * @param branchId  ID of the branch.
     * @param reorderId ID of the reorder.
     * @return Reorder.
     */
    Reorder getById(long branchId, long reorderId);

    /**
     * Returns all reorders of a branch.
     *
     * @param branchId ID of the branch.
     * @param status   Optional status filter.
     * @return List of all reorders from a branch.
     */
    List<Reorder> getAllByBranch(long branchId, @Nullable ReorderStatus status);

    /**
     * Returns all reorders.
     *
     * @param status Status of the reorders.
     * @return List of all reorders.
     */
    List<WarehouseEntity<Reorder>> getAllByStatus(ReorderStatus status);

    /**
     * Adds a reorder for the branch.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @param quantity  Number of articles to reorder.
     * @return Reorder.
     */
    Reorder create(long branchId, long articleId, int quantity);

    /**
     * Updates the status of a reorder of the branch.
     *
     * @param branchId  ID of the branch.
     * @param reorderId ID of the reorder.
     * @param status    New reorder status.
     * @return Reorder.
     */
    Reorder updateStatus(long branchId, long reorderId, ReorderStatus status);

    /**
     * Updates the status of a reorder of the branch.
     *
     * @param branchId  ID of the branch.
     * @param reorderId ID of the reorder.
     * @param quantity  New reorder quantity.
     * @return Reorder.
     */
    Reorder updateQuantity(long branchId, long reorderId, int quantity);

    /**
     * Deletes a reorder from the branch.
     *
     * @param branchId  ID of the branch.
     * @param reorderId ID of the reorder.
     * @return True if successful, false if not.
     */
    boolean delete(long branchId, long reorderId);

    /**
     * Counts the number of reordered articles of the specified article of the branch.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @return Number of reordered articles.
     */
    int countReorderedArticles(long branchId, long articleId);
}
