package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testcases for the in-memory product catalogue.
 */
final class ProductCatalogueMemoryTest {

    private ProductCatalogue productCatalogue;

    @BeforeEach
    void beforeEach() {
        final Article article1 = new Article(1L, "Test1", new BigDecimal("5.25"), 0);
        final Article article2 = new Article(2L, "Test2", new BigDecimal("9.95"), 1);
        this.productCatalogue = new ProductCatalogueMemory();
        this.productCatalogue.create(article1);
        this.productCatalogue.create(article2);
    }

    @Test
    void testGetAll() {
        assertThat(this.productCatalogue.getAll()).hasSize(2);
    }

    @Test
    void testGetFirst() {
        final Article article = this.productCatalogue.getById(1L);
        assertThat(article).isNotNull();
        assertThat(article.articleId()).isEqualTo(1);
        assertThat(article.name()).isEqualTo("Test1");
        assertThat(article.price()).isEqualTo(new BigDecimal("5.25"));
        assertThat(article.stock()).isEqualTo(0);
    }


    @Test
    void testCreate() {
        final Article article = new Article(3L, "Test3", new BigDecimal("13.70"), 5);
        final Article createdArticle = productCatalogue.create(article);
        assertThat(createdArticle.articleId()).isEqualTo(article.articleId());
        assertThat(createdArticle.name()).isEqualTo(article.name());
        assertThat(createdArticle.price()).isEqualTo(article.price());
        assertThat(createdArticle.stock()).isEqualTo(article.stock());
        assertThat(productCatalogue.getAll()).hasSize(3);
    }

    @Test
    void testUpdateExistingArticle() {
        final Article article = new Article(1L, "Test99", new BigDecimal("99.95"), 10);
        final Article updatedArticle = productCatalogue.update(article.articleId(), article);
        assertThat(updatedArticle.articleId()).isEqualTo(article.articleId());
        assertThat(updatedArticle.name()).isEqualTo(article.name());
        assertThat(updatedArticle.price()).isEqualTo(article.price());
        assertThat(updatedArticle.stock()).isEqualTo(article.stock());
        assertThat(productCatalogue.getAll()).hasSize(2);
    }

    @Test
    void testUpdateNotExistingArticle() {
        final Article article = new Article(5L, "Test5", new BigDecimal("25.00"), 2);
        final Article updatedArticle = productCatalogue.update(article.articleId(), article);
        assertThat(updatedArticle.articleId()).isEqualTo(article.articleId());
        assertThat(updatedArticle.name()).isEqualTo(article.name());
        assertThat(updatedArticle.price()).isEqualTo(article.price());
        assertThat(updatedArticle.stock()).isEqualTo(article.stock());
        assertThat(productCatalogue.getAll()).hasSize(3);
    }

    @Test
    void testDeleteExistingArticle() {
        final boolean deleted = productCatalogue.delete(2L);
        assertThat(deleted).isTrue();
        assertThat(productCatalogue.getById(2L)).isNull();
        assertThat(productCatalogue.getAll()).hasSize(1);
    }

    @Test
    void testDeleteNotExistingArticle() {
        final boolean deleted = productCatalogue.delete(5L);
        assertThat(deleted).isFalse();
        assertThat(productCatalogue.getAll()).hasSize(2);
    }
}
