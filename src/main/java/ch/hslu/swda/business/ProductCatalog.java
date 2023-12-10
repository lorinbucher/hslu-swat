package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
import ch.hslu.swda.entities.WarehouseEntity;

import java.util.List;
import java.util.Map;

/**
 * Management of the product catalog.
 */
public interface ProductCatalog {

    /**
     * Returns the article with the specified article ID in the branch's catalog.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @return Article.
     */
    Article getById(long branchId, long articleId);

    /**
     * Returns the articles with the specified article IDs in the branch's catalog.
     *
     * @param branchId   ID of the branch.
     * @param articleIds IDs of the article.
     * @return Pairs of article ID and article.
     */
    Map<Long, Article> getById(long branchId, List<Long> articleIds);

    /**
     * Returns all articles in the product catalog of the branch.
     *
     * @param branchId ID of the branch.
     * @return List of all articles.
     */
    List<Article> getAll(long branchId);

    /**
     * Adds an article to the catalog of the branch.
     *
     * @param branchId ID of the branch.
     * @param article  Article.
     * @return Article.
     */
    Article create(long branchId, Article article);

    /**
     * Updates an article in the catalog of the branch.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @param article   Article.
     * @return Article.
     */
    Article update(long branchId, long articleId, Article article);

    /**
     * Deletes an article from the catalog of the branch.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @return True if successful, false if not.
     */
    boolean delete(long branchId, long articleId);

    /**
     * Changes the stocked items of an article by the specified amount.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @param amount    Amount to change the stocked items by.
     * @return True if successful, false if not.
     */
    boolean changeStock(long branchId, long articleId, int amount);

    /**
     * Changes the reserved items of an article by the specified amount.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @param amount    Amount to change the reserved items by.
     * @return True if successful, false if not.
     */
    boolean changeReserved(long branchId, long articleId, int amount);

    /**
     * Returns all articles with low stock from all branches.
     *
     * @return List of all articles with low stock.
     */
    List<WarehouseEntity<Article>> getLowStock();
}
