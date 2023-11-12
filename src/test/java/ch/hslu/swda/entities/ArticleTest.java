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
public class ArticleTest {

    @Test
    void testFromDocument() {
        Document document = new Document()
                .append("branchId", 1L)
                .append("articleId", 5L)
                .append("name", "Test")
                .append("price", "5.95")
                .append("minStock", 5)
                .append("stock", 10);
        final Article article = Article.fromDocument(document);
        assertThat(article.articleId()).isEqualTo(5L);
        assertThat(article.name()).isEqualTo("Test");
        assertThat(article.price().compareTo(new BigDecimal("5.95"))).isEqualTo(0);
        assertThat(article.minStock()).isEqualTo(5);
        assertThat(article.stock()).isEqualTo(10);
    }

    @Test
    void testToDocument() {
        final Article article = new Article(5L, "Test", new BigDecimal("5.95"), 5, 10);
        Document document = Article.toDocument(article);
        assertThat(document.getLong("articleId")).isEqualTo(article.articleId());
        assertThat(document.getString("name")).isEqualTo(article.name());
        assertThat(new BigDecimal(document.getString("price"))).isEqualTo(article.price());
        assertThat(document.getInteger("minStock")).isEqualTo(article.minStock());
        assertThat(document.getInteger("stock")).isEqualTo(article.stock());
    }

    @Test
    void testArticleIdInvalid() {
        assertThatThrownBy(() -> new Article(0L, "Test", new BigDecimal("1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be lower than 1");
    }

    @Test
    void testArticleIdValid() {
        final Article article = new Article(1L, "Test", new BigDecimal("1.00"), 1, 1);
        assertThat(article.articleId()).isEqualTo(1L);
    }

    @Test
    void testNameNull() {
        assertThatThrownBy(() -> new Article(1L, null, new BigDecimal("1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name should not be blank");
    }

    @Test
    void testNameInvalid() {
        assertThatThrownBy(() -> new Article(1L, "", new BigDecimal("1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name should not be blank");
    }

    @Test
    void testNameValid() {
        final Article article = new Article(1L, "Test", new BigDecimal("1.00"), 1, 1);
        assertThat(article.name()).isEqualTo("Test");
    }

    @Test
    void testPriceNull() {
        assertThatThrownBy(() -> new Article(1L, "Test", null, 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceNegative() {
        assertThatThrownBy(() -> new Article(1L, "Test", new BigDecimal("-1.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceZero() {
        assertThatThrownBy(() -> new Article(1L, "Test", new BigDecimal("0.00"), 1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price should be 0.05 or higher");
    }

    @Test
    void testPriceValid() {
        final Article article = new Article(1L, "Test", new BigDecimal("0.05"), 1, 1);
        assertThat(article.price().compareTo(new BigDecimal("0.05"))).isEqualTo(0);
    }

    @Test
    void testPriceRounded() {
        final Article article = new Article(1L, "Test", new BigDecimal("15.2649895"), 1, 1);
        assertThat(article.price().compareTo(new BigDecimal("15.26"))).isEqualTo(0);
    }

    @Test
    void testMinStockInvalid() {
        assertThatThrownBy(() -> new Article(1L, "Test", new BigDecimal("1.00"), -1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minStock should not be lower than 0");
    }

    @Test
    void testMinStockValid() {
        final Article article = new Article(1L, "Test", new BigDecimal("1.00"), 0, 1);
        assertThat(article.minStock()).isEqualTo(0);
    }

    @Test
    void testStockInvalid() {
        assertThatThrownBy(() -> new Article(1L, "Test", new BigDecimal("1.00"), 1, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("stock should not be lower than 0");
    }

    @Test
    void testStockValid() {
        final Article article = new Article(1L, "Test", new BigDecimal("1.00"), 1, 0);
        assertThat(article.stock()).isEqualTo(0);
    }

    @Test
    void testArticleNotEqual() {
        final Article article1 = new Article(1L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(2L, "Test2", new BigDecimal("2.00"), 1, 1);
        assertThat(article1).isNotEqualTo(article2);
    }

    @Test
    void testArticleEqual() {
        final Article article1 = new Article(1L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(1L, "Test2", new BigDecimal("2.00"), 1, 1);
        assertThat(article1).isEqualTo(article2);
    }

    @Test
    void testArticleHashCodeDiffers() {
        final Article article1 = new Article(1L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(2L, "Test2", new BigDecimal("2.00"), 1, 1);
        assertThat(article1).doesNotHaveSameHashCodeAs(article2);
    }

    @Test
    void testArticleHashCode() {
        final Article article1 = new Article(1L, "Test1", new BigDecimal("1.00"), 1, 0);
        final Article article2 = new Article(1L, "Test2", new BigDecimal("2.00"), 1, 1);
        assertThat(article1).hasSameHashCodeAs(article2);
    }

    @Test
    void testJsonObject() {
        final Article article = new Article(1L, "Test", new BigDecimal("50.25"), 1, 5);
        String articleJson = "{\"articleId\":1,\"name\":\"Test\",\"price\":50.25,\"minStock\":1,\"stock\":5}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            assertThat(mapper.writeValueAsString(article)).isEqualTo(articleJson);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
