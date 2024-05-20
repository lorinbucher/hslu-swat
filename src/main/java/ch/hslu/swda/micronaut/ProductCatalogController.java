package ch.hslu.swda.micronaut;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.Article;
import ch.hslu.swda.micro.MessagePublisher;
import ch.hslu.swda.micro.Routes;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for the product catalog.
 */
@Controller("/api/v1/catalog")
public final class ProductCatalogController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogController.class);

    private final ProductCatalog productCatalog;
    private final MessagePublisher<LogEventDTO> eventLogger;

    @Inject
    public ProductCatalogController(ProductCatalog productCatalog, MessagePublisher<LogEventDTO> eventLogger) {
        this.productCatalog = productCatalog;
        this.eventLogger = eventLogger;
    }

    /**
     * Get all articles in the product catalog of the branch.
     * <p>
     * Roles: Branch Manager, Seller
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
     * <p>
     * Roles: Branch Manager, Seller
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
     * <p>
     * Roles: Branch Manager
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
        this.eventLogger.sendMessage(Routes.LOG_EVENT, new LogEventDTO(branchId, "article.added", message));
        return created;
    }

    /**
     * Updates an article in the product catalog of the branch.
     * <p>
     * Roles: Branch Manager
     *
     * @param branchId  ID of the branch.
     * @param articleId ID of the article.
     * @param name      Name of the article.
     * @param price     Price per article.
     * @param minStock  Minimum number of articles in stock.
     * @return Updated article.
     */
    @Tag(name = "catalog")
    @Patch("/{branchId}/{articleId}")
    public Article update(final long branchId, final long articleId,
                          @JsonProperty String name, @JsonProperty BigDecimal price, @JsonProperty int minStock) {
        Article updated = productCatalog.update(branchId, articleId, name, price, minStock);
        LOG.info("REST: Article {} from branch {} updated.", updated, branchId);
        String message = "Updated article " + articleId + " in catalog";
        this.eventLogger.sendMessage(Routes.LOG_EVENT, new LogEventDTO(branchId, "article.changed", message));
        return updated;
    }

    /**
     * Removes an article from the product catalog.
     * <p>
     * Roles: Branch Manager
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
            this.eventLogger.sendMessage(Routes.LOG_EVENT, new LogEventDTO(branchId, "article.removed", message));
        }
    }

    @Error(exception = IllegalArgumentException.class)
    public HttpResponse<JsonError> invalidStatus(HttpRequest request, IllegalArgumentException ex) {
        JsonError error = new JsonError(ex.getMessage())
                .link(Link.SELF, Link.of(request.getUri()));
        return HttpResponse.<JsonError>badRequest().body(error);
    }
}
