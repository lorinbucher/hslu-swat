package ch.hslu.swda.micronaut;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.Article;
import ch.hslu.swda.micro.EventLogger;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for the product catalog.
 */
@Controller("/api/v1/catalog")
public final class ProductCatalogController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogController.class);

    @Inject
    private EventLogger eventLogger;

    @Inject
    private ProductCatalog productCatalog;

    /**
     * Get all articles in the product catalog of the branch.
     *
     * @param branchId ID of the branch.
     * @return List of all articles.
     */
    @Tag(name = "catalog")
    @Get("/{branchId}")
    public List<Article> getAll(final long branchId) {
        final List<Article> articles = productCatalog.getAll(branchId);
        LOG.info("REST: All {} articles from branch {} returned.", articles.size(), branchId);
        return articles;
    }

    /**
     * Get article with the specified id from the product catalog of the branch.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @return Article.
     */
    @Tag(name = "catalog")
    @Get("/{branchId}/{articleId}")
    public Article get(final long branchId, final long articleId) {
        final Article article = productCatalog.getById(branchId, articleId);
        LOG.info("REST: Article {} from branch {} {}.", articleId, branchId, article != null ? "returned" : "not found");
        return article;
    }

    /**
     * Adds a new article to the product catalog of the branch.
     *
     * @param branchId ID of the branch.
     * @param article  Article.
     * @return Added article.
     */
    @Tag(name = "catalog")
    @Status(HttpStatus.CREATED)
    @Post("/{branchId}")
    public Article create(final long branchId, @Body final Article article) {
        final Article created = productCatalog.create(branchId, article);
        LOG.info("REST: Article {} added to branch {}.", created, branchId);
        String message = "Added article " + article.articleId() + " in catalog";
        this.eventLogger.publishLog(new LogEventDTO(branchId, "article.added", message));
        return created;
    }

    /**
     * Updates an article in the product catalog of the branch.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @param article   Article.
     * @return Updated article.
     */
    @Tag(name = "catalog")
    @Put("/{branchId}/{articleId}")
    public Article update(final long branchId, final long articleId, @Body final Article article) {
        Article updated = productCatalog.update(branchId, articleId, article);
        LOG.info("REST: Article {} from branch {} updated.", updated, branchId);
        String message = "Updated article " + articleId + " in catalog";
        this.eventLogger.publishLog(new LogEventDTO(branchId, "article.changed", message));
        return updated;
    }

    /**
     * Removes an article from the product catalog.
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     */
    @Tag(name = "catalog")
    @Delete("/{branchId}/{articleId}")
    public void delete(final long branchId, final long articleId) {
        final boolean deleted = productCatalog.delete(branchId, articleId);
        LOG.info("REST: Article {} {}removed from branch {}.", articleId, deleted ? "" : "not ", branchId);
        if (deleted) {
            String message = "Removed article " + articleId + " from catalog";
            this.eventLogger.publishLog(new LogEventDTO(branchId, "article.removed", message));
        }
    }
}
