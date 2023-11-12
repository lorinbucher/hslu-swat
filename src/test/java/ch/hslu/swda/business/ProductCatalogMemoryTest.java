package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testcases for the in-memory product catalog.
 */
final class ProductCatalogMemoryTest {

    private ProductCatalog productCatalog;

    @BeforeEach
    void beforeEach() {
        final Article article1 = new Article(1L, "Test1", new BigDecimal("5.25"), 1, 0);
        final Article article2 = new Article(2L, "Test2", new BigDecimal("9.95"), 1, 1);
        this.productCatalog = new ProductCatalogMemory();
        this.productCatalog.create(1, article1);
        this.productCatalog.create(1, article2);
    }

    @Test
    void testGetAll() {
        assertThat(this.productCatalog.getAll(1L)).hasSize(2);
    }

    @Test
    void testGetAllInvalidBranch() {
        assertThat(this.productCatalog.getAll(2L)).hasSize(0);
    }

    @Test
    void testGetFirst() {
        final Article article = this.productCatalog.getById(1L, 1L);
        assertThat(article).isNotNull();
        assertThat(article.articleId()).isEqualTo(1);
        assertThat(article.name()).isEqualTo("Test1");
        assertThat(article.price()).isEqualTo(new BigDecimal("5.25"));
        assertThat(article.stock()).isEqualTo(0);
    }


    @Test
    void testCreate() {
        final Article article = new Article(3L, "Test3", new BigDecimal("13.70"), 1, 5);
        final Article createdArticle = productCatalog.create(2L, article);
        assertThat(createdArticle.articleId()).isEqualTo(article.articleId());
        assertThat(createdArticle.name()).isEqualTo(article.name());
        assertThat(createdArticle.price()).isEqualTo(article.price());
        assertThat(createdArticle.stock()).isEqualTo(article.stock());
        assertThat(productCatalog.getAll(1L)).hasSize(2);
        assertThat(productCatalog.getAll(2L)).hasSize(1);
    }

    @Test
    void testUpdateExistingArticle() {
        final Article article = new Article(1L, "Test99", new BigDecimal("99.95"), 1, 10);
        final Article updatedArticle = productCatalog.update(1L, article.articleId(), article);
        assertThat(updatedArticle.articleId()).isEqualTo(article.articleId());
        assertThat(updatedArticle.name()).isEqualTo(article.name());
        assertThat(updatedArticle.price()).isEqualTo(article.price());
        assertThat(updatedArticle.stock()).isEqualTo(article.stock());
        assertThat(productCatalog.getAll(1L)).hasSize(2);
    }

    @Test
    void testUpdateNotExistingArticle() {
        final Article article = new Article(5L, "Test5", new BigDecimal("25.00"), 1, 2);
        final Article updatedArticle = productCatalog.update(1L, article.articleId(), article);
        assertThat(updatedArticle.articleId()).isEqualTo(article.articleId());
        assertThat(updatedArticle.name()).isEqualTo(article.name());
        assertThat(updatedArticle.price()).isEqualTo(article.price());
        assertThat(updatedArticle.stock()).isEqualTo(article.stock());
        assertThat(productCatalog.getAll(1L)).hasSize(3);
    }

    @Test
    void testDeleteExistingArticle() {
        final boolean deleted = productCatalog.delete(1L, 2L);
        assertThat(deleted).isTrue();
        assertThat(productCatalog.getById(1L, 2L)).isNull();
        assertThat(productCatalog.getAll(1L)).hasSize(1);
    }

    @Test
    void testDeleteNotExistingArticle() {
        final boolean deleted = productCatalog.delete(1L, 5L);
        assertThat(deleted).isFalse();
        assertThat(productCatalog.getAll(1L)).hasSize(2);
    }
}
