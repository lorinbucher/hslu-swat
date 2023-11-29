package ch.hslu.swda.entities;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the warehouse article entity.
 */
class WarehouseArticleTest {

    @Test
    void testNotEqualBranch() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseArticle warehouseArticle1 = new WarehouseArticle(1L, article);
        final WarehouseArticle warehouseArticle2 = new WarehouseArticle(2L, article);
        assertThat(warehouseArticle1).isNotEqualTo(warehouseArticle2);
    }

    @Test
    void testNotEqualArticle() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(100002L, "Test2", new BigDecimal("2.00"), 1, 1);
        final WarehouseArticle warehouseArticle1 = new WarehouseArticle(1L, article1);
        final WarehouseArticle warehouseArticle2 = new WarehouseArticle(1L, article2);
        assertThat(warehouseArticle1).isNotEqualTo(warehouseArticle2);
    }

    @Test
    void testEqual() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseArticle warehouseArticle1 = new WarehouseArticle(1L, article);
        final WarehouseArticle warehouseArticle2 = new WarehouseArticle(1L, article);
        assertThat(warehouseArticle1).isEqualTo(warehouseArticle1);
        assertThat(warehouseArticle1).isEqualTo(warehouseArticle2);
    }

    @Test
    void testHashCodeDiffersBranch() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseArticle warehouseArticle1 = new WarehouseArticle(1L, article);
        final WarehouseArticle warehouseArticle2 = new WarehouseArticle(2L, article);
        assertThat(warehouseArticle1).doesNotHaveSameHashCodeAs(warehouseArticle2);
    }

    @Test
    void testHashCodeDiffersArticle() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(100002L, "Test2", new BigDecimal("2.00"), 1, 1);
        final WarehouseArticle warehouseArticle1 = new WarehouseArticle(1L, article1);
        final WarehouseArticle warehouseArticle2 = new WarehouseArticle(1L, article2);
        assertThat(warehouseArticle1).doesNotHaveSameHashCodeAs(warehouseArticle2);
    }

    @Test
    void testHashCode() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseArticle warehouseArticle1 = new WarehouseArticle(1L, article);
        final WarehouseArticle warehouseArticle2 = new WarehouseArticle(1L, article);
        assertThat(warehouseArticle1).hasSameHashCodeAs(warehouseArticle2);
    }

    @Test
    void testFromDocument() {
        Document document = new Document()
                .append("branchId", 1L)
                .append("articleId", 100005L)
                .append("name", "Test")
                .append("price", "5.95")
                .append("minStock", 5)
                .append("stock", 10);
        final WarehouseArticle warehouseArticle = WarehouseArticle.fromDocument(document);
        assertThat(warehouseArticle.branchId()).isEqualTo(1L);
        assertThat(warehouseArticle.article().articleId()).isEqualTo(100005L);
        assertThat(warehouseArticle.article().name()).isEqualTo("Test");
        assertThat(warehouseArticle.article().price().compareTo(new BigDecimal("5.95"))).isEqualTo(0);
        assertThat(warehouseArticle.article().minStock()).isEqualTo(5);
        assertThat(warehouseArticle.article().stock()).isEqualTo(10);
    }

    @Test
    void testToDocument() {
        final Article article = new Article(100005L, "Test", new BigDecimal("5.95"), 5, 10);
        final WarehouseArticle warehouseArticle = new WarehouseArticle(1L, article);
        Document document = WarehouseArticle.toDocument(warehouseArticle);
        assertThat(document.getLong("branchId")).isEqualTo(warehouseArticle.branchId());
        assertThat(document.getLong("articleId")).isEqualTo(article.articleId());
        assertThat(document.getString("name")).isEqualTo(article.name());
        assertThat(new BigDecimal(document.getString("price"))).isEqualTo(article.price());
        assertThat(document.getInteger("minStock")).isEqualTo(article.minStock());
        assertThat(document.getInteger("stock")).isEqualTo(article.stock());
    }
}
