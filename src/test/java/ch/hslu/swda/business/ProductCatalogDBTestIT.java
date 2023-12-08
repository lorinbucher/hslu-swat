package ch.hslu.swda.business;

import ch.hslu.swda.entities.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the product catalog db class.
 */
@Testcontainers
class ProductCatalogDBTestIT {

    private static final String IMAGE = "mongo:4.2.24";

    private ProductCatalog productCatalog;

    @Container
    private final GenericContainer<?> mongoContainer = new GenericContainer<>(DockerImageName.parse(IMAGE))
            .withExposedPorts(27017)
            .withStartupTimeout(Duration.ofSeconds(30))
            .waitingFor(Wait.forLogMessage(".*waiting for connections on port 27017.*\\n", 1));

    @BeforeEach
    void initializeEnv() {
        String host = mongoContainer.getHost() + ":" + mongoContainer.getMappedPort(27017);
        productCatalog = new ProductCatalogDB(host, "", "");
        Article article1 = new Article(100001L, "Test1", new BigDecimal("5.25"), 1, 1);
        Article article2 = new Article(100002L, "Test2", new BigDecimal("9.95"), 2, 2);
        productCatalog.create(1L, article1);
        productCatalog.create(1L, article2);
    }

    @Test
    void testGetByIdExisting() {
        Article existing = new Article(100001L, "Test1", new BigDecimal("5.25"), 1, 1);
        Article article = productCatalog.getById(1L, 100001L);
        assertThat(article).isNotNull();
        assertThat(article).isEqualTo(existing);
        assertThat(article.name()).isEqualTo(existing.name());
        assertThat(article.price()).isEqualTo(existing.price());
        assertThat(article.minStock()).isEqualTo(existing.minStock());
        assertThat(article.stock()).isEqualTo(existing.stock());
    }

    @Test
    void testGetByIdNotExisting() {
        Article article = productCatalog.getById(2L, 100001L);
        assertThat(article).isNull();
    }

    @Test
    void testGetAll() {
        List<Article> articles = productCatalog.getAll(1L);
        assertThat(articles).hasSize(2);
    }

    @Test
    void testGetAllEmpty() {
        List<Article> articles = productCatalog.getAll(2L);
        assertThat(articles).isEmpty();
    }

    @Test
    void testCreateExisting() {
        Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 5, 5);
        Article created = productCatalog.create(1L, article);
        assertThat(productCatalog.getAll(1L)).hasSize(2);
        assertThat(created).isEqualTo(article);
        assertThat(created.name()).isNotEqualTo(article.name());
        assertThat(created.price()).isNotEqualTo(article.price());
        assertThat(created.minStock()).isNotEqualTo(article.minStock());
        assertThat(created.stock()).isNotEqualTo(article.stock());
    }

    @Test
    void testCreateNotExisting() {
        Article article = new Article(100005L, "Test", new BigDecimal("1.00"), 5, 5);
        Article created = productCatalog.create(1L, article);
        assertThat(productCatalog.getAll(1L)).hasSize(3);
        assertThat(productCatalog.getById(1L, 100005L)).isEqualTo(article);
        assertThat(created).isEqualTo(article);
        assertThat(created.name()).isEqualTo(article.name());
        assertThat(created.price()).isEqualTo(article.price());
        assertThat(created.minStock()).isEqualTo(article.minStock());
        assertThat(created.stock()).isEqualTo(article.stock());
    }

    @Test
    void testUpdateExisting() {
        Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 5, 5);
        Article updated = productCatalog.update(1L, 100001L, article);
        assertThat(productCatalog.getAll(1L)).hasSize(2);
        assertThat(productCatalog.getById(1L, 100001L)).isEqualTo(article);
        assertThat(updated).isEqualTo(article);
        assertThat(updated.name()).isEqualTo(article.name());
        assertThat(updated.price()).isEqualTo(article.price());
        assertThat(updated.minStock()).isEqualTo(article.minStock());
        assertThat(updated.stock()).isEqualTo(article.stock());
    }

    @Test
    void testUpdateExistingIdMismatch() {
        Article article = new Article(100005L, "Test", new BigDecimal("1.00"), 5, 5);
        Article updated = productCatalog.update(1L, 100001L, article);
        assertThat(productCatalog.getAll(1L)).hasSize(2);
        assertThat(updated.articleId()).isEqualTo(100001L);
        assertThat(updated.name()).isEqualTo(article.name());
        assertThat(updated.price()).isEqualTo(article.price());
        assertThat(updated.minStock()).isEqualTo(article.minStock());
        assertThat(updated.stock()).isEqualTo(article.stock());
    }

    @Test
    void testUpdateNotExisting() {
        Article article = new Article(100005L, "Test", new BigDecimal("1.00"), 5, 5);
        Article updated = productCatalog.update(1L, 100005L, article);
        assertThat(productCatalog.getAll(1L)).hasSize(3);
        assertThat(productCatalog.getById(1L, 100005L)).isEqualTo(article);
        assertThat(updated).isEqualTo(article);
    }

    @Test
    void testDeleteExisting() {
        boolean result = productCatalog.delete(1L, 100001L);
        assertThat(result).isTrue();
        assertThat(productCatalog.getAll(1L)).hasSize(1);
        assertThat(productCatalog.getById(1L, 100001L)).isNull();
    }

    @Test
    void testDeleteNotExisting() {
        boolean result = productCatalog.delete(1L, 100005L);
        assertThat(result).isFalse();
        assertThat(productCatalog.getAll(1L)).hasSize(2);
    }

    @Test
    void testChangeStockNotExisting() {
        boolean result = productCatalog.changeStock(1L, 100005L, 2);
        assertThat(result).isFalse();
    }

    @Test
    void testChangeStockIncrease() {
        assertThat(productCatalog.getById(1L, 100001L).stock()).isEqualTo(1);
        boolean result = productCatalog.changeStock(1L, 100001L, 2);
        assertThat(result).isTrue();
        assertThat(productCatalog.getById(1L, 100001L).stock()).isEqualTo(3);
    }

    @Test
    void testChangeStockDecrease() {
        assertThat(productCatalog.getById(1L, 100001L).stock()).isEqualTo(1);
        boolean result = productCatalog.changeStock(1L, 100001L, -1);
        assertThat(result).isTrue();
        assertThat(productCatalog.getById(1L, 100001L).stock()).isEqualTo(0);
    }

    @Test
    void testChangeStockDecreaseMore() {
        assertThat(productCatalog.getById(1L, 100001L).stock()).isEqualTo(1);
        boolean result = productCatalog.changeStock(1L, 100001L, -2);
        assertThat(result).isFalse();
        assertThat(productCatalog.getById(1L, 100001L).stock()).isEqualTo(1);
    }
}
