package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.DeliveriesMemory;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogMemory;
import ch.hslu.swda.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the article message processor.
 */
class ArticleMessageProcessorTest {

    private ProductCatalog productCatalog;

    @BeforeEach
    void initializeEnv() {
        productCatalog = new ProductCatalogMemory();
        productCatalog.create(1, new Article(100001L, "Article 1", new BigDecimal("5.25"), 1, 1));
        productCatalog.create(1, new Article(100002L, "Article 2", new BigDecimal("9.95"), 2, 2));
    }

    @Test
    void testInvalidMessageReceived() {
        ArticleMessageProcessor processor = new ArticleMessageProcessor(productCatalog);
        String message = "{}";
        assertThatThrownBy(() -> processor.process(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot parse received article message");
    }

    @Test
    void testValidMessageReceived() {
        ArticleMessageProcessor processor = new ArticleMessageProcessor(productCatalog);
        String message = "{\"branchId\":1,\"orderNumber\":5,\"articles\":[100001,100005]}";
        String response = processor.process(message);
        assertThat(response).isEqualTo("{\"branchId\":1,\"orderNumber\":5," +
                "\"articles\":[{\"articleId\":100001,\"name\":\"Article 1\",\"price\":5.25}]," +
                "\"error\":[\"article 100005 not found in catalog\"]}");
    }
}
