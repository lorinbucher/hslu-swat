package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalogue;
import ch.hslu.swda.entities.Article;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for the product catalogue.
 */
@Controller("/api/v1/catalogue")
public class ProductCatalogueController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogueController.class);

    @Inject
    private ProductCatalogue productCatalogue;

    /**
     * Get all articles in the product catalogue.
     *
     * @return List of all articles.
     */
    @Tag(name = "Catalogue")
    @Get("/")
    public List<Article> getAll() {
        final List<Article> articles = productCatalogue.getAll();
        LOG.info("REST: All {} articles returned.", articles.size());
        return articles;
    }

    /**
     * Get article with the specified id from the product catalogue.
     *
     * @param id id of the article.
     * @return Article.
     */
    @Tag(name = "Catalogue")
    @Get("/{id}")
    public Article get(final long id) {
        final Article article = productCatalogue.getById(id);
        LOG.info("REST: Article {} {}.", id, article != null ? "returned" : "not found");
        return article;
    }

    /**
     * Adds a new article to the product catalogue.
     *
     * @param article Article.
     * @return Added article.
     */
    @Tag(name = "Catalogue")
    @Post("/")
    public Article create(@Body final Article article) {
        final Article created = productCatalogue.create(article);
        LOG.info("REST: Article {} added.", created);
        return article;
    }

    /**
     * Updates an article in the product catalogue.
     *
     * @param id      id of the article.
     * @param article Article.
     * @return Updated article.
     */
    @Tag(name = "Catalogue")
    @Put("/{id}")
    public Article update(final long id, @Body final Article article) {
        Article updated = productCatalogue.update(id, article);
        LOG.info("REST: Article {} updated.", updated);
        return updated;
    }

    /**
     * Removes an article from the product catalogue.
     *
     * @param id id of the article.
     */
    @Tag(name = "Catalogue")
    @Delete("/{id}")
    public void delete(final int id) {
        final boolean deleted = productCatalogue.delete(id);
        LOG.info("REST: Article with id={} {}removed.", id, deleted ? "" : "not ");
    }
}
