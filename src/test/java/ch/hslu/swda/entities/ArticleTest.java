package ch.hslu.swda.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the article entity.
 */
class ArticleTest {

    @Test
    void testArticleIdInvalidMin() {
        BigDecimal price = new BigDecimal("1.00");
        assertThatThrownBy(() -> new Article(99999L, "Test", price, 1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be lower than 100000");
    }

    @Test
    void testArticleIdInvalidMax() {
        BigDecimal price = new BigDecimal("1.00");
        assertThatThrownBy(() -> new Article(Integer.MAX_VALUE + 1L, "Test", price, 1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be higher than " + Integer.MAX_VALUE);
    }

    @Test
    void testArticleIdValid() {
        final Article article = new Article(100000L, "Test", new BigDecimal("1.00"), 1, 1, 1);
        assertThat(article.articleId()).isEqualTo(100000L);
    }

    @Test
    void testNameNull() {
        BigDecimal price = new BigDecimal("1.00");
        assertThatThrownBy(() -> new Article(100001L, null, price, 1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name should not be blank");
    }

    @Test
    void testNameInvalid() {
        BigDecimal price = new BigDecimal("1.00");
        assertThatThrownBy(() -> new Article(100001L, "", price, 1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name should not be blank");
    }

    @Test
    void testNameValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 1, 1, 1);
        assertThat(article.name()).isEqualTo("Test");
    }

    @Test
    void testPriceNull() {
        assertThatThrownBy(() -> new Article(100001L, "Test", null, 1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceNegative() {
        BigDecimal price = new BigDecimal("-1.00");
        assertThatThrownBy(() -> new Article(100001L, "Test", price, 1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceZero() {
        BigDecimal price = new BigDecimal("0.00");
        assertThatThrownBy(() -> new Article(100001L, "Test", price, 1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("0.05"), 1, 1, 1);
        assertThat(article.price()).isEqualByComparingTo(new BigDecimal("0.05"));
    }

    @Test
    void testPriceRounded() {
        final Article article = new Article(100001L, "Test", new BigDecimal("15.2649895"), 1, 1, 1);
        assertThat(article.price()).isEqualByComparingTo(new BigDecimal("15.26"));
    }

    @Test
    void testMinStockInvalid() {
        BigDecimal price = new BigDecimal("1.00");
        assertThatThrownBy(() -> new Article(100001L, "Test", price, -1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minStock should not be lower than 0");
    }

    @Test
    void testMinStockValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 0, 1, 1);
        assertThat(article.minStock()).isZero();
    }

    @Test
    void testStockInvalid() {
        BigDecimal price = new BigDecimal("1.00");
        assertThatThrownBy(() -> new Article(100001L, "Test", price, 1, -1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("stock should not be lower than 0");
    }

    @Test
    void testStockValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 1, 0, 1);
        assertThat(article.stock()).isZero();
    }

    @Test
    void testReservedInvalid() {
        BigDecimal price = new BigDecimal("1.00");
        assertThatThrownBy(() -> new Article(100001L, "Test", price, 1, 0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("reserved should not be lower than 0");
    }

    @Test
    void testReservedValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 1, 1, 0);
        assertThat(article.reserved()).isZero();
    }

    @Test
    void testNotEqual() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0, 0);
        final Article article2 = new Article(100002L, "Test2", new BigDecimal("2.00"), 1, 1, 1);
        assertThat(article1)
                .isNotEqualTo(1L)
                .isNotEqualTo(article2);
    }

    @Test
    void testEqual() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0, 0);
        final Article article2 = new Article(100001L, "Test2", new BigDecimal("2.00"), 1, 1, 1);
        assertThat(article1)
                .isEqualTo(article1)
                .isEqualTo(article2);
    }

    @Test
    void testHashCodeDiffers() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0, 0);
        final Article article2 = new Article(100002L, "Test2", new BigDecimal("2.00"), 1, 1, 1);
        assertThat(article1).doesNotHaveSameHashCodeAs(article2);
    }

    @Test
    void testHashCode() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0, 0);
        final Article article2 = new Article(100001L, "Test2", new BigDecimal("2.00"), 1, 1, 1);
        assertThat(article1).hasSameHashCodeAs(article2);
    }

    @Test
    void testJsonObject() {
        final Article article = new Article(100001L, "Test", new BigDecimal("50.25"), 1, 5, 5);
        String articleJson = "{\"articleId\":100001,\"name\":\"Test\",\"price\":50.25,\""
                + "minStock\":1,\"stock\":5,\"reserved\":5}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(article)).isEqualTo(articleJson);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }

    @Test
    void testFromDocument() {
        Document document = new Document()
                .append("articleId", 100005L)
                .append("name", "Test")
                .append("price", "5.95")
                .append("minStock", 5)
                .append("stock", 10)
                .append("reserved", 10);
        final Article article = new Article(document);
        assertThat(article.articleId()).isEqualTo(100005L);
        assertThat(article.name()).isEqualTo("Test");
        assertThat(article.price()).isEqualByComparingTo(new BigDecimal("5.95"));
        assertThat(article.minStock()).isEqualTo(5);
        assertThat(article.stock()).isEqualTo(10);
        assertThat(article.reserved()).isEqualTo(10);
    }

    @Test
    void testToDocument() {
        final Article article = new Article(100005L, "Test", new BigDecimal("5.95"), 5, 10, 10);
        Document document = article.toDocument();
        assertThat(document.getLong("articleId")).isEqualTo(article.articleId());
        assertThat(document.getString("name")).isEqualTo(article.name());
        assertThat(new BigDecimal(document.getString("price"))).isEqualTo(article.price());
        assertThat(document.getInteger("minStock")).isEqualTo(article.minStock());
        assertThat(document.getInteger("stock")).isEqualTo(article.stock());
        assertThat(document.getInteger("reserved")).isEqualTo(article.reserved());
    }
}
