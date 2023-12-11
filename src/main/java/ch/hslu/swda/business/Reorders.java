package ch.hslu.swda.business;

import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
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
     * @param status   Status filter of the reorders.
     * @return List of all reorders.
     */
    List<Reorder> getAll(long branchId, @Nullable final ReorderStatus status);

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
     * @param status    Reorder status.
     * @return Reorder.
     */
    Reorder updateStatus(long branchId, long reorderId, ReorderStatus status);

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
