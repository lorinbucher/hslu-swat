package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogMemory;
import ch.hslu.swda.dto.OrderDTO;
import ch.hslu.swda.entities.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the article message processor.
 */
class ArticleMessageProcessorTest {

    private MessageListenerDummy listener;
    private MessagePublisherDummy<OrderDTO> publisher;

    @BeforeEach
    void initializeEnv() {
        ProductCatalog productCatalog = new ProductCatalogMemory();
        productCatalog.create(1, new Article(100001L, "Article 1", new BigDecimal("5.25"), 1, 1, 1));
        productCatalog.create(1, new Article(100002L, "Article 2", new BigDecimal("9.95"), 2, 2, 2));

        listener = new MessageListenerDummy();
        publisher = new MessagePublisherDummy<>();
        ArticleMessageProcessor processor = new ArticleMessageProcessor(listener, publisher, productCatalog);
        processor.run();
    }

    @Test
    void testInvalidMessageReceived() {
        String message = "{}";
        listener.mockMessage(Routes.ARTICLE_GET, message);
        String response = publisher.getMessage(Routes.ARTICLE_RETURN);
        assertThat(response).isNull();
    }

    @Test
    void testValidMessageReceivedError() {
        String message = "{\"branchId\":1,\"orderNumber\":5,\"articles\":[100005]}";
        listener.mockMessage(Routes.ARTICLE_GET, message);
        String response = publisher.getMessage(Routes.ARTICLE_RETURN);
        assertThat(response).isEqualTo("{\"branchId\":1,\"orderNumber\":5,"
                + "\"articles\":[],\"error\":[\"article 100005 not found in catalog\"]}");
    }

    @Test
    void testValidMessageReceivedNoError() {
        String message = "{\"branchId\":1,\"orderNumber\":5,\"articles\":[100001]}";
        listener.mockMessage(Routes.ARTICLE_GET, message);
        String response = publisher.getMessage(Routes.ARTICLE_RETURN);
        assertThat(response).isEqualTo("{\"branchId\":1,\"orderNumber\":5,"
                + "\"articles\":[{\"articleId\":100001,\"name\":\"Article 1\",\"price\":5.25}],\"error\":[]}");
    }

    @Test
    void testValidMessageReceivedMixed() {
        String message = "{\"branchId\":1,\"orderNumber\":5,\"articles\":[100001,100005]}";
        listener.mockMessage(Routes.ARTICLE_GET, message);
        String response = publisher.getMessage(Routes.ARTICLE_RETURN);
        assertThat(response).isEqualTo("{\"branchId\":1,\"orderNumber\":5,"
                + "\"articles\":[{\"articleId\":100001,\"name\":\"Article 1\",\"price\":5.25}],"
                + "\"error\":[\"article 100005 not found in catalog\"]}");
    }
}
