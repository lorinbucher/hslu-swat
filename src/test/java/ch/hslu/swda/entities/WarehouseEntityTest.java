package ch.hslu.swda.entities;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the warehouse entity.
 */
class WarehouseEntityTest {

    @Test
    void testBranchIdInvalid() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        assertThatThrownBy(() -> new WarehouseEntity<Article>(0L, article))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("branchId should not be lower than 1");
    }

    @Test
    void testBranchIdValid() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseEntity<Article> warehouseEntity = new WarehouseEntity<>(1L, article);
        assertThat(warehouseEntity.branchId()).isEqualTo(1L);
    }

    @Test
    void testNotEqualBranch() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseEntity<Article> warehouseEntity1 = new WarehouseEntity<>(1L, article);
        final WarehouseEntity<Article> warehouseEntity2 = new WarehouseEntity<>(2L, article);
        assertThat(warehouseEntity1).isNotEqualTo(warehouseEntity2);
    }

    @Test
    void testNotEqualEntity() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(100002L, "Test2", new BigDecimal("2.00"), 1, 1);
        final WarehouseEntity<Article> warehouseEntity1 = new WarehouseEntity<>(1L, article1);
        final WarehouseEntity<Article> warehouseEntity2 = new WarehouseEntity<>(2L, article2);
        assertThat(warehouseEntity1).isNotEqualTo(warehouseEntity2);
    }

    @Test
    void testEqual() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseEntity<Article> warehouseEntity1 = new WarehouseEntity<>(1L, article);
        final WarehouseEntity<Article> warehouseEntity2 = new WarehouseEntity<>(1L, article);
        assertThat(warehouseEntity1).isEqualTo(warehouseEntity1);
        assertThat(warehouseEntity1).isEqualTo(warehouseEntity2);
    }

    @Test
    void testHashCodeDiffersBranch() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseEntity<Article> warehouseEntity1 = new WarehouseEntity<>(1L, article);
        final WarehouseEntity<Article> warehouseEntity2 = new WarehouseEntity<>(2L, article);
        assertThat(warehouseEntity1).doesNotHaveSameHashCodeAs(warehouseEntity2);
    }

    @Test
    void testHashCodeDiffersEntity() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(100002L, "Test2", new BigDecimal("2.00"), 1, 1);
        final WarehouseEntity<Article> warehouseEntity1 = new WarehouseEntity<>(1L, article1);
        final WarehouseEntity<Article> warehouseEntity2 = new WarehouseEntity<>(2L, article2);
        assertThat(warehouseEntity1).doesNotHaveSameHashCodeAs(warehouseEntity2);
    }

    @Test
    void testHashCode() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseEntity<Article> warehouseEntity1 = new WarehouseEntity<>(1L, article);
        final WarehouseEntity<Article> warehouseEntity2 = new WarehouseEntity<>(1L, article);
        assertThat(warehouseEntity1).hasSameHashCodeAs(warehouseEntity2);
    }

    @Test
    void testToDocument() {
        final Article article = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final WarehouseEntity<Article> warehouseEntity = new WarehouseEntity<>(1L, article);
        Document document = warehouseEntity.toDocument();
        assertThat(document.getLong("branchId")).isEqualTo(1L);
        assertThat(document.getLong("articleId")).isEqualTo(article.articleId());
        assertThat(document.getString("name")).isEqualTo(article.name());
        assertThat(new BigDecimal(document.getString("price"))).isEqualTo(article.price());
        assertThat(document.getInteger("minStock")).isEqualTo(article.minStock());
        assertThat(document.getInteger("stock")).isEqualTo(article.stock());
    }
}
