package ch.hslu.swda.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the article entity.
 */
public class ArticleTest {

    @Test
    void testArticleIdInvalidMin() {
        assertThatThrownBy(() -> new Article(99999L, "Test", new BigDecimal("1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be lower than 100000");
    }

    @Test
    void testArticleIdInvalidMax() {
        assertThatThrownBy(() -> new Article(Integer.MAX_VALUE + 1L, "Test", new BigDecimal("1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be higher than " + Integer.MAX_VALUE);
    }

    @Test
    void testArticleIdValid() {
        final Article article = new Article(100000L, "Test", new BigDecimal("1.00"), 1, 1);
        assertThat(article.articleId()).isEqualTo(100000L);
    }

    @Test
    void testNameNull() {
        assertThatThrownBy(() -> new Article(100001L, null, new BigDecimal("1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name should not be blank");
    }

    @Test
    void testNameInvalid() {
        assertThatThrownBy(() -> new Article(100001L, "", new BigDecimal("1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name should not be blank");
    }

    @Test
    void testNameValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 1, 1);
        assertThat(article.name()).isEqualTo("Test");
    }

    @Test
    void testPriceNull() {
        assertThatThrownBy(() -> new Article(100001L, "Test", null, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceNegative() {
        assertThatThrownBy(() -> new Article(100001L, "Test", new BigDecimal("-1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceZero() {
        assertThatThrownBy(() -> new Article(100001L, "Test", new BigDecimal("0.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("0.05"), 1, 1);
        assertThat(article.price().compareTo(new BigDecimal("0.05"))).isEqualTo(0);
    }

    @Test
    void testPriceRounded() {
        final Article article = new Article(100001L, "Test", new BigDecimal("15.2649895"), 1, 1);
        assertThat(article.price().compareTo(new BigDecimal("15.26"))).isEqualTo(0);
    }

    @Test
    void testMinStockInvalid() {
        assertThatThrownBy(() -> new Article(100001L, "Test", new BigDecimal("1.00"), -1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minStock should not be lower than 0");
    }

    @Test
    void testMinStockValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 0, 1);
        assertThat(article.minStock()).isEqualTo(0);
    }

    @Test
    void testStockInvalid() {
        assertThatThrownBy(() -> new Article(100001L, "Test", new BigDecimal("1.00"), 1, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("stock should not be lower than 0");
    }

    @Test
    void testStockValid() {
        final Article article = new Article(100001L, "Test", new BigDecimal("1.00"), 1, 0);
        assertThat(article.stock()).isEqualTo(0);
    }

    @Test
    void testNotEqual() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(100002L, "Test2", new BigDecimal("2.00"), 1, 1);
        assertThat(article1).isNotEqualTo(article2);
    }

    @Test
    void testEqual() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(100001L, "Test2", new BigDecimal("2.00"), 1, 1);
        assertThat(article1).isEqualTo(article1);
        assertThat(article1).isEqualTo(article2);
    }

    @Test
    void testHashCodeDiffers() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(100002L, "Test2", new BigDecimal("2.00"), 1, 1);
        assertThat(article1).doesNotHaveSameHashCodeAs(article2);
    }

    @Test
    void testHashCode() {
        final Article article1 = new Article(100001L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(100001L, "Test2", new BigDecimal("2.00"), 1, 1);
        assertThat(article1).hasSameHashCodeAs(article2);
    }

    @Test
    void testJsonObject() {
        final Article article = new Article(100001L, "Test", new BigDecimal("50.25"), 1, 5);
        String articleJson = "{\"articleId\":100001,\"name\":\"Test\",\"price\":50.25,\"minStock\":1,\"stock\":5}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(article)).isEqualTo(articleJson);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
