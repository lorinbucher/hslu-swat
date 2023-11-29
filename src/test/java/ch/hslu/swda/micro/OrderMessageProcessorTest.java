package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.DeliveriesMemory;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogMemory;
import ch.hslu.swda.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMessageProcessorTest {

//    private Deliveries deliveries;
//    private ProductCatalog productCatalog;
//
//    @BeforeEach
//    void initializeEnv() {
//        deliveries = new DeliveriesMemory();
//        deliveries.create(1, new Delivery(1L, DeliveryStatus.NEW, List.of(
//                new DeliveryArticle(100001L, 1, DeliveryArticleStatus.PROCESSING)
//        )));
//        deliveries.create(1, new Delivery(2L, DeliveryStatus.COMPLETED, List.of(
//                new DeliveryArticle(100002L, 2, DeliveryArticleStatus.DELIVERED)
//        )));
//
//        productCatalog = new ProductCatalogMemory();
//        productCatalog.create(1, new Article(100001L, "Article 1", new BigDecimal("5.25"), 1, 1));
//        productCatalog.create(1, new Article(100002L, "Article 2", new BigDecimal("9.95"), 2, 2));
//    }
//
//    @Test
//    void testDeleteNotExistingDelivery() {
//        OrderMessageProcessor processor = new OrderMessageProcessor(productCatalog);
//        String message = createMessageString(5L, List.of());
//        String response = processor.process(message);
//        assertThat(response).isEqualTo("{\"branchId\":1,\"orderNumber\":5,\"articles\":[]," +
//                "\"error\":[\"processing delivery failed\"]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(2);
//    }
//
//    @Test
//    void testNewDelivery() {
//        OrderMessageProcessor processor = new OrderMessageProcessor(productCatalog);
//        String message = createMessageString(5L, List.of(100001L));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":5,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(3);
//        assertThat(deliveries.getById(1L, 5L).status()).isEqualTo(DeliveryStatus.NEW);
//        assertThat(deliveries.getById(1L, 5L).articles()).hasSize(1);
//        assertThat(deliveries.getById(1L, 5L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.PROCESSING);
//    }
//
//    @Test
//    void testExistingDeliveryStateNew() {
//        OrderMessageProcessor processor = new OrderMessageProcessor(productCatalog);
//        String message = createMessageString(1L, List.of(100002L));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":1,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(2);
//        assertThat(deliveries.getById(1L, 1L).status()).isEqualTo(DeliveryStatus.NEW);
//        assertThat(deliveries.getById(1L, 1L).articles()).hasSize(1);
//        assertThat(deliveries.getById(1L, 1L).articles().get(0).articleId()).isEqualTo(100002L);
//        assertThat(deliveries.getById(1L, 1L).articles().get(0).quantity()).isEqualTo(2);
//        assertThat(deliveries.getById(1L, 1L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.PROCESSING);
//    }
//
//    @Test
//    void testExistingDeliveryStateChanged() {
//        deliveries.create(1L, new Delivery(3L, DeliveryStatus.CHANGED, List.of(
//                new DeliveryArticle(100001L, 1, DeliveryArticleStatus.RESERVED),
//                new DeliveryArticle(100002L, 2, DeliveryArticleStatus.PROCESSING)
//        )));
//
//        OrderMessageProcessor processor = new OrderMessageProcessor(productCatalog);
//        String message = createMessageString(3L, List.of(100001L));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":3,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(3);
//        assertThat(deliveries.getById(1L, 3L).status()).isEqualTo(DeliveryStatus.CHANGED);
//        assertThat(deliveries.getById(1L, 3L).articles()).hasSize(2);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).articleId()).isEqualTo(100001L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).quantity()).isEqualTo(1);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.RESERVED);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).articleId()).isEqualTo(100001L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).quantity()).isEqualTo(5);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.PROCESSING);
//    }
//
//    @Test
//    void testExistingDeliveryStateWaitingOrReady() {
//        deliveries.create(1L, new Delivery(3L, DeliveryStatus.WAITING, List.of(
//                new DeliveryArticle(100001L, 1, DeliveryArticleStatus.RESERVED),
//                new DeliveryArticle(100002L, 2, DeliveryArticleStatus.ORDERED)
//        )));
//
//        OrderMessageProcessor processor = new OrderMessageProcessor(productCatalog);
//        String message = createMessageString(3L, List.of(100001L));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":3,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(3);
//        assertThat(deliveries.getById(1L, 3L).status()).isEqualTo(DeliveryStatus.CHANGED);
//        assertThat(deliveries.getById(1L, 3L).articles()).hasSize(3);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).articleId()).isEqualTo(100001L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).quantity()).isEqualTo(1);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.RESERVED);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).articleId()).isEqualTo(100002L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).quantity()).isEqualTo(2);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.ORDERED);
//        assertThat(deliveries.getById(1L, 3L).articles().get(2).articleId()).isEqualTo(100001L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(2).quantity()).isEqualTo(5);
//        assertThat(deliveries.getById(1L, 3L).articles().get(2).status()).isEqualTo(DeliveryArticleStatus.PROCESSING);
//    }
//
//    @Test
//    void testExistingDeliveryStateCompleted() {
//        OrderMessageProcessor processor = new OrderMessageProcessor(productCatalog);
//        String message = createMessageString(2L, List.of(100001L));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":2,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[\"processing delivery failed\"]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(2);
//        assertThat(deliveries.getById(1L, 2L).status()).isEqualTo(DeliveryStatus.COMPLETED);
//        assertThat(deliveries.getById(1L, 2L).articles()).hasSize(1);
//        assertThat(deliveries.getById(1L, 2L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.DELIVERED);
//    }
//
//    @Test
//    void testDeleteNotExistingDelivery() {
//        OrderMessageProcessor processor = new OrderMessageProcessor(deliveries, productCatalog);
//        String message = createMessageString(5L, Map.of());
//        String response = processor.process(message);
//        assertThat(response).isEqualTo("{\"branchId\":1,\"orderNumber\":5,\"articles\":[]," +
//                "\"error\":[\"processing delivery failed\"]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(2);
//    }
//
//    @Test
//    void testNewDelivery() {
//        OrderMessageProcessor processor = new OrderMessageProcessor(deliveries, productCatalog);
//        String message = createMessageString(5L, Map.of(100001L, 1));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":5,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(3);
//        assertThat(deliveries.getById(1L, 5L).status()).isEqualTo(DeliveryStatus.NEW);
//        assertThat(deliveries.getById(1L, 5L).articles()).hasSize(1);
//        assertThat(deliveries.getById(1L, 5L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.PROCESSING);
//    }
//
//    @Test
//    void testExistingDeliveryStateNew() {
//        OrderMessageProcessor processor = new OrderMessageProcessor(deliveries, productCatalog);
//        String message = createMessageString(1L, Map.of(100002L, 2));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":1,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(2);
//        assertThat(deliveries.getById(1L, 1L).status()).isEqualTo(DeliveryStatus.NEW);
//        assertThat(deliveries.getById(1L, 1L).articles()).hasSize(1);
//        assertThat(deliveries.getById(1L, 1L).articles().get(0).articleId()).isEqualTo(100002L);
//        assertThat(deliveries.getById(1L, 1L).articles().get(0).quantity()).isEqualTo(2);
//        assertThat(deliveries.getById(1L, 1L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.PROCESSING);
//    }
//
//    @Test
//    void testExistingDeliveryStateChanged() {
//        deliveries.create(1L, new Delivery(3L, DeliveryStatus.CHANGED, List.of(
//                new DeliveryArticle(100001L, 1, DeliveryArticleStatus.RESERVED),
//                new DeliveryArticle(100002L, 2, DeliveryArticleStatus.PROCESSING)
//        )));
//
//        OrderMessageProcessor processor = new OrderMessageProcessor(deliveries, productCatalog);
//        String message = createMessageString(3L, Map.of(100001L, 5));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":3,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(3);
//        assertThat(deliveries.getById(1L, 3L).status()).isEqualTo(DeliveryStatus.CHANGED);
//        assertThat(deliveries.getById(1L, 3L).articles()).hasSize(2);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).articleId()).isEqualTo(100001L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).quantity()).isEqualTo(1);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.RESERVED);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).articleId()).isEqualTo(100001L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).quantity()).isEqualTo(5);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.PROCESSING);
//    }
//
//    @Test
//    void testExistingDeliveryStateWaitingOrReady() {
//        deliveries.create(1L, new Delivery(3L, DeliveryStatus.WAITING, List.of(
//                new DeliveryArticle(100001L, 1, DeliveryArticleStatus.RESERVED),
//                new DeliveryArticle(100002L, 2, DeliveryArticleStatus.ORDERED)
//        )));
//
//        OrderMessageProcessor processor = new OrderMessageProcessor(deliveries, productCatalog);
//        String message = createMessageString(3L, Map.of(100001L, 5));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":3,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(3);
//        assertThat(deliveries.getById(1L, 3L).status()).isEqualTo(DeliveryStatus.CHANGED);
//        assertThat(deliveries.getById(1L, 3L).articles()).hasSize(3);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).articleId()).isEqualTo(100001L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).quantity()).isEqualTo(1);
//        assertThat(deliveries.getById(1L, 3L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.RESERVED);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).articleId()).isEqualTo(100002L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).quantity()).isEqualTo(2);
//        assertThat(deliveries.getById(1L, 3L).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.ORDERED);
//        assertThat(deliveries.getById(1L, 3L).articles().get(2).articleId()).isEqualTo(100001L);
//        assertThat(deliveries.getById(1L, 3L).articles().get(2).quantity()).isEqualTo(5);
//        assertThat(deliveries.getById(1L, 3L).articles().get(2).status()).isEqualTo(DeliveryArticleStatus.PROCESSING);
//    }
//
//    @Test
//    void testExistingDeliveryStateCompleted() {
//        OrderMessageProcessor processor = new OrderMessageProcessor(deliveries, productCatalog);
//        String message = createMessageString(2L, Map.of(100001L, 1));
//        String response = processor.process(message);
//        assertThat(response).startsWith("{\"branchId\":1,\"orderNumber\":2,\"articles\":[{");
//        assertThat(response).endsWith("}],\"error\":[\"processing delivery failed\"]}");
//        assertThat(deliveries.getAll(1L, null)).hasSize(2);
//        assertThat(deliveries.getById(1L, 2L).status()).isEqualTo(DeliveryStatus.COMPLETED);
//        assertThat(deliveries.getById(1L, 2L).articles()).hasSize(1);
//        assertThat(deliveries.getById(1L, 2L).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.DELIVERED);
//    }
//
//    private String createMessageString(long orderNumber, Map<Long, Integer> articles) {
//        StringBuilder message = new StringBuilder();
//        message.append("{\"branchId\":").append(1L);
//        message.append(",\"orderNumber\":").append(orderNumber);
//        message.append(",\"articles\":[");
//        Iterator<Map.Entry<Long, Integer>> articlesIt = articles.entrySet().iterator();
//        while (articlesIt.hasNext()) {
//            Map.Entry<Long, Integer> entry = articlesIt.next();
//            message.append("{\"articleId\":").append(entry.getKey());
//            message.append(",\"quantity\":").append(entry.getValue());
//            message.append(articlesIt.hasNext() ? "}," : "}");
//        }
//        message.append("]}");
//        return message.toString();
//
//    }
}
