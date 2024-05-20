package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.DeliveriesMemory;
import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryArticle;
import ch.hslu.swda.entities.DeliveryArticleStatus;
import ch.hslu.swda.entities.DeliveryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMessageProcessorTest {

    private Deliveries deliveries;
    private MessageListenerDummy listener;

    @BeforeEach
    void initializeEnv() {
        deliveries = new DeliveriesMemory();
        deliveries.create(1, new Delivery(1L, DeliveryStatus.NEW, List.of(
                new DeliveryArticle(100001L, 1, DeliveryArticleStatus.ADD)
        )));
        deliveries.create(1, new Delivery(2L, DeliveryStatus.COMPLETED, List.of(
                new DeliveryArticle(100002L, 2, DeliveryArticleStatus.DELIVERED)
        )));

        listener = new MessageListenerDummy();
        OrderMessageProcessor processor = new OrderMessageProcessor(listener, deliveries);
        processor.run();
    }

    @Test
    void testInvalidMessageReceived() {
        listener.mockMessage(Routes.ORDER, "{}");
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
    }

    @Test
    void testDeleteNotExistingDelivery() {
        String message = createMessageString(5L, List.of());
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
    }

    @Test
    void testDeleteExistingDelivery() {
        String message = createMessageString(1L, List.of());
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).status()).isEqualTo(DeliveryStatus.MODIFIED);
        assertThat(deliveries.getById(1L, 1L).articles()).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).quantity()).isEqualTo(1);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.ADD);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).quantity()).isEqualTo(1);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.REMOVE);
    }

    @Test
    void testNewDeliveryNewOrder() {
        String article = createMessageStringArticle(100001L, 1, null);
        String message = createMessageString(5L, List.of(article));
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(3);
        assertThat(deliveries.getById(1L, 5L).status()).isEqualTo(DeliveryStatus.NEW);
        assertThat(deliveries.getById(1L, 5L).articles()).hasSize(1);
        assertThat(deliveries.getById(1L, 5L).articles().get(0).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 5L).articles().get(0).quantity()).isEqualTo(1);
        assertThat(deliveries.getById(1L, 5L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.ADD);
    }

    @Test
    void testNewDeliveryExistingOrder() {
        String article = createMessageStringArticle(100001L, 1, DeliveryArticleStatus.REMOVE);
        String message = createMessageString(5L, List.of(article));
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(3);
        assertThat(deliveries.getById(1L, 5L).status()).isEqualTo(DeliveryStatus.NEW);
        assertThat(deliveries.getById(1L, 5L).articles()).hasSize(1);
        assertThat(deliveries.getById(1L, 5L).articles().get(0).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 5L).articles().get(0).quantity()).isEqualTo(1);
        assertThat(deliveries.getById(1L, 5L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.REMOVE);
    }

    @Test
    void testExistingDeliveryStatusNew() {
        String article = createMessageStringArticle(100005L, 5, DeliveryArticleStatus.ADD);
        String message = createMessageString(1L, List.of(article));
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).status()).isEqualTo(DeliveryStatus.MODIFIED);
        assertThat(deliveries.getById(1L, 1L).articles()).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).quantity()).isEqualTo(1);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.ADD);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).articleId()).isEqualTo(100005L);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).quantity()).isEqualTo(5);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.ADD);
    }

    @Test
    void testExistingDeliveryStatusModified() {
        deliveries.updateStatus(1L, 1L, DeliveryStatus.MODIFIED);
        String article = createMessageStringArticle(100001L, 5, DeliveryArticleStatus.MODIFY);
        String message = createMessageString(1L, List.of(article));
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).status()).isEqualTo(DeliveryStatus.MODIFIED);
        assertThat(deliveries.getById(1L, 1L).articles()).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).quantity()).isEqualTo(1);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.ADD);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).quantity()).isEqualTo(5);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.MODIFY);
    }

    @Test
    void testExistingDeliveryStatusWaiting() {
        deliveries.updateStatus(1L, 1L, DeliveryStatus.WAITING);
        String article = createMessageStringArticle(100001L, 5, DeliveryArticleStatus.REMOVE);
        String message = createMessageString(1L, List.of(article));
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).status()).isEqualTo(DeliveryStatus.MODIFIED);
        assertThat(deliveries.getById(1L, 1L).articles()).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).quantity()).isEqualTo(1);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.ADD);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).quantity()).isEqualTo(5);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.REMOVE);
    }

    @Test
    void testExistingDeliveryStatusReady() {
        deliveries.updateStatus(1L, 1L, DeliveryStatus.READY);
        String article = createMessageStringArticle(100005L, 5, DeliveryArticleStatus.ADD);
        String message = createMessageString(1L, List.of(article));
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).status()).isEqualTo(DeliveryStatus.MODIFIED);
        assertThat(deliveries.getById(1L, 1L).articles()).hasSize(2);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).articleId()).isEqualTo(100001L);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).quantity()).isEqualTo(1);
        assertThat(deliveries.getById(1L, 1L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.ADD);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).articleId()).isEqualTo(100005L);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).quantity()).isEqualTo(5);
        assertThat(deliveries.getById(1L, 1L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.ADD);
    }

    @Test
    void testExistingDeliveryStatusDelivered() {
        deliveries.updateStatus(1L, 2L, DeliveryStatus.DELIVERED);
        String article = createMessageStringArticle(100001L, 5, DeliveryArticleStatus.MODIFY);
        String message = createMessageString(2L, List.of(article));
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveries.getById(1L, 2L).status()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(deliveries.getById(1L, 2L).articles()).hasSize(1);
        assertThat(deliveries.getById(1L, 2L).articles().get(0).articleId()).isEqualTo(100002L);
        assertThat(deliveries.getById(1L, 2L).articles().get(0).quantity()).isEqualTo(2);
        assertThat(deliveries.getById(1L, 2L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.DELIVERED);
    }

    @Test
    void testExistingDeliveryStatusCompleted() {
        String article = createMessageStringArticle(100001L, 5, DeliveryArticleStatus.REMOVE);
        String message = createMessageString(2L, List.of(article));
        listener.mockMessage(Routes.ORDER, message);
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveries.getById(1L, 2L).status()).isEqualTo(DeliveryStatus.COMPLETED);
        assertThat(deliveries.getById(1L, 2L).articles()).hasSize(1);
        assertThat(deliveries.getById(1L, 2L).articles().get(0).articleId()).isEqualTo(100002L);
        assertThat(deliveries.getById(1L, 2L).articles().get(0).quantity()).isEqualTo(2);
        assertThat(deliveries.getById(1L, 2L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.DELIVERED);
    }

    private String createMessageString(long orderNumber, List<String> articles) {
        StringBuilder message = new StringBuilder();
        message.append("{\"branchId\":").append(1L);
        message.append(",\"orderNumber\":").append(orderNumber);
        message.append(",\"articles\":[");
        Iterator<String> articlesIt = articles.iterator();
        while (articlesIt.hasNext()) {
            message.append(articlesIt.next());
            if (articlesIt.hasNext()) {
                message.append(",");
            }
        }
        message.append("]}");
        return message.toString();
    }

    private String createMessageStringArticle(long articleId, int quantity, DeliveryArticleStatus action) {
        StringBuilder message = new StringBuilder();
        message.append("{\"articleId\":").append(articleId);
        message.append(",\"quantity\":").append(quantity);
        if (action != null) {
            message.append(",\"action\":\"").append(action.name()).append("\"");
        }
        message.append("}");
        return message.toString();
    }
}
