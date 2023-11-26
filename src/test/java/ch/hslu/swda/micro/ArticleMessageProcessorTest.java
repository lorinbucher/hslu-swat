package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogMemory;
import ch.hslu.swda.entities.Article;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the article message processor.
 */
class ArticleMessageProcessorTest {

    @Test
    void testInvalidMessageReceived() {
        ArticleMessageProcessor processor = new ArticleMessageProcessor(new ProductCatalogMemory());
        String message = "{}";
        assertThatThrownBy(() -> processor.process(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot parse received article message");
    }

    @Test
    void testValidMessageReceived() {
        ArticleMessageProcessor processor = new ArticleMessageProcessor(new ProductCatalogMemory());
        String message = "{\"orderNumber\":1,\"branchId\":1,\"articles\":[]}";
        String response = processor.process(message);
        assertThat(response).isEqualTo("{\"orderNumber\":1,\"branchId\":1,\"articles\":[],\"error\":[]}");
    }

    @Test
    void testWithArticles() {
        ProductCatalog productCatalog = new ProductCatalogMemory();
        productCatalog.create(1, new Article(100001L, "Test", new BigDecimal("5.25"), 1, 1));
        ArticleMessageProcessor processor = new ArticleMessageProcessor(productCatalog);
        String message = "{\"orderNumber\":1,\"branchId\":1,\"articles\":[100001,100002]}";
        String response = processor.process(message);
        assertThat(response).isEqualTo("{\"orderNumber\":1,\"branchId\":1,\"articles\":" +
                "[{\"articleId\":100001,\"name\":\"Test\",\"price\":5.25}],\"error\":[\"article 100002 not found in catalog\"]}");
    }
}
