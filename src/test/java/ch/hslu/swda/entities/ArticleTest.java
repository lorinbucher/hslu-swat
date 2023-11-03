package ch.hslu.swda.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for the article entity.
 */
public class ArticleTest {

    @Test
    void testArticleIdInvalid() {
        assertThatThrownBy(() -> new Article(0, "Test", new BigDecimal("1.00"), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be lower than 1");
    }

    @Test
    void testArticleIdValid() {
        Article article = new Article(1, "Test", new BigDecimal("1.00"), 1);
        assertThat(article.articleId()).isEqualTo(1);
    }

    @Test
    void testNameNull() {
        assertThatThrownBy(() -> new Article(1, null, new BigDecimal("1.00"), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name should not be blank");
    }

    @Test
    void testNameInvalid() {
        assertThatThrownBy(() -> new Article(1, "", new BigDecimal("1.00"), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name should not be blank");
    }

    @Test
    void testNameValid() {
        Article article = new Article(1, "Test", new BigDecimal("1.00"), 1);
        assertThat(article.name()).isEqualTo("Test");
    }

    @Test
    void testPriceNull() {
        assertThatThrownBy(() -> new Article(1, "Test", null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceNegative() {
        assertThatThrownBy(() -> new Article(1, "Test", new BigDecimal("-1.00"), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceZero() {
        assertThatThrownBy(() -> new Article(1, "Test", new BigDecimal("0.00"), 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceValid() {
        Article article = new Article(1, "Test", new BigDecimal("0.05"), 1);
        assertThat(article.price().compareTo(new BigDecimal("0.05"))).isEqualTo(0);
    }

    @Test
    void testPriceRounded() {
        Article article = new Article(1, "Test", new BigDecimal("15.2649895"), 1);
        assertThat(article.price().compareTo(new BigDecimal("15.26"))).isEqualTo(0);
    }

    @Test
    void testStockInvalid() {
        assertThatThrownBy(() -> new Article(1, "Test", new BigDecimal("1.00"), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("stock should not be lower than 0");
    }

    @Test
    void testStockValid() {
        Article article = new Article(1, "Test", new BigDecimal("1.00"), 0);
        assertThat(article.stock()).isEqualTo(0);
    }

    @Test
    void testArticleEqual() {
        Article article1 = new Article(1, "Test", new BigDecimal("1.00"), 0);
        Article article2 = new Article(1, "Test1", new BigDecimal("2.00"), 1);
        assertThat(article1).isEqualTo(article2);
    }

    @Test
    void testArticleHashCode() {
        Article article1 = new Article(1, "Test", new BigDecimal("1.00"), 0);
        Article article2 = new Article(1, "Test1", new BigDecimal("2.00"), 1);
        assertThat(article1).hasSameHashCodeAs(article2);
    }

    @Test
    void testJsonObject() {
        Article article = new Article(1, "Test", new BigDecimal("50.25"), 5);
        String articleJson = "{\"articleId\":1,\"name\":\"Test\",\"price\":50.25,\"stock\":5}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            assertThat(mapper.writeValueAsString(article)).isEqualTo(articleJson);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
