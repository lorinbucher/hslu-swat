package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.DeliveriesMemory;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogMemory;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the delivery processor.
 */
class DeliveryProcessorTest {

    private MessagePublisherDummy<LogEventDTO> publisher;
    private ProductCatalog catalog;
    private Deliveries deliveries;

    @BeforeEach
    void initializeEnv() {
        catalog = new ProductCatalogMemory();
        catalog.create(1, new Article(100001L, "Article 1", new BigDecimal("5.25"), 5, 5, 3));
        catalog.create(1, new Article(100002L, "Article 2", new BigDecimal("9.95"), 10, 10, 6));
        publisher = new MessagePublisherDummy<>();
        deliveries = new DeliveriesMemory();
    }

    @Test
    void testProcessDeliveredDeliveries() {
        DeliveryArticle article1 = new DeliveryArticle(100001L, 10, DeliveryArticleStatus.RESERVED);
        DeliveryArticle article2 = new DeliveryArticle(100002L, 3, DeliveryArticleStatus.RESERVED);
        deliveries.create(1L, new Delivery(1L, DeliveryStatus.DELIVERED, List.of(article1, article2)));
        deliveries.create(1L, new Delivery(2L, DeliveryStatus.DELIVERED, List.of(article2)));
        assertThat(deliveries.getAllByStatus(DeliveryStatus.DELIVERED)).hasSize(2);

        DeliveryProcessor processor = new DeliveryProcessor(publisher, catalog, deliveries);
        processor.run();
        processor.run();
        assertThat(deliveries.getAllByStatus(DeliveryStatus.DELIVERED)).hasSize(1);
        assertThat(deliveries.getAllByStatus(DeliveryStatus.COMPLETED)).hasSize(1);

        List<Delivery> processed = deliveries.getAllByBranch(1L, null);
        assertThat(processed).hasSize(2);
        assertThat(processed.get(0).status()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(processed.get(1).status()).isEqualTo(DeliveryStatus.COMPLETED);
        assertThat(processed.get(0).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.RESERVED);
        assertThat(processed.get(0).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.DELIVERED);
        assertThat(processed.get(1).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.DELIVERED);
        assertThat(catalog.getById(1L, 100001L).stock()).isEqualTo(5);
        assertThat(catalog.getById(1L, 100001L).reserved()).isEqualTo(3);
        assertThat(catalog.getById(1L, 100002L).stock()).isEqualTo(4);
        assertThat(catalog.getById(1L, 100002L).reserved()).isEqualTo(0);
    }

    @Test
    void testProcessNewAndModifiedDeliveriesAdd() {
        DeliveryArticle article1 = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.ADD);
        DeliveryArticle article2 = new DeliveryArticle(100002L, 3, DeliveryArticleStatus.RESERVED);
        DeliveryArticle article1Add = new DeliveryArticle(100001L, 4, DeliveryArticleStatus.ADD);
        DeliveryArticle article2Add = new DeliveryArticle(100002L, 6, DeliveryArticleStatus.ADD);
        deliveries.create(1L, new Delivery(1L, DeliveryStatus.NEW, List.of(article1, article2, article1Add)));
        deliveries.create(1L, new Delivery(2L, DeliveryStatus.MODIFIED, List.of(article2, article1Add, article2Add)));
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);

        DeliveryProcessor processor = new DeliveryProcessor(publisher, catalog, deliveries);
        processor.run();

        List<Delivery> processed = deliveries.getAllByBranch(1L, null);
        assertThat(processed).hasSize(2);
        assertThat(processed.get(0).articles()).hasSize(2);
        assertThat(processed.get(1).articles()).hasSize(2);
        assertThat(processed.get(0).articles()).allMatch(a -> a.status() == DeliveryArticleStatus.RESERVED);
        assertThat(processed.get(1).articles()).allMatch(a -> a.status() == DeliveryArticleStatus.RESERVED);
        assertThat(catalog.getById(1L, 100001L).stock()).isEqualTo(5);
        assertThat(catalog.getById(1L, 100001L).reserved()).isEqualTo(11);
        assertThat(catalog.getById(1L, 100002L).stock()).isEqualTo(10);
        assertThat(catalog.getById(1L, 100002L).reserved()).isEqualTo(9);
    }

    @Test
    void testProcessNewAndModifiedDeliveriesModify() {
        DeliveryArticle article1 = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.ADD);
        DeliveryArticle article2 = new DeliveryArticle(100002L, 3, DeliveryArticleStatus.RESERVED);
        DeliveryArticle article1Mod = new DeliveryArticle(100001L, 4, DeliveryArticleStatus.MODIFY);
        DeliveryArticle article2Mod = new DeliveryArticle(100002L, 6, DeliveryArticleStatus.MODIFY);
        deliveries.create(1L, new Delivery(1L, DeliveryStatus.NEW, List.of(article1, article2, article1Mod)));
        deliveries.create(1L, new Delivery(2L, DeliveryStatus.MODIFIED, List.of(article2, article1Mod, article2Mod)));
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);

        DeliveryProcessor processor = new DeliveryProcessor(publisher, catalog, deliveries);
        processor.run();

        List<Delivery> processed = deliveries.getAllByBranch(1L, null);
        assertThat(processed).hasSize(2);
        assertThat(processed.get(0).articles()).hasSize(2);
        assertThat(processed.get(1).articles()).hasSize(2);
        assertThat(processed.get(0).articles()).allMatch(a -> a.status() == DeliveryArticleStatus.RESERVED);
        assertThat(processed.get(1).articles()).allMatch(a -> a.status() == DeliveryArticleStatus.RESERVED);
        assertThat(catalog.getById(1L, 100001L).stock()).isEqualTo(5);
        assertThat(catalog.getById(1L, 100001L).reserved()).isEqualTo(11);
        assertThat(catalog.getById(1L, 100002L).stock()).isEqualTo(10);
        assertThat(catalog.getById(1L, 100002L).reserved()).isEqualTo(9);
    }

    @Test
    void testProcessNewAndModifiedDeliveriesRemove() {
        DeliveryArticle article1 = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.ADD);
        DeliveryArticle article2 = new DeliveryArticle(100002L, 3, DeliveryArticleStatus.RESERVED);
        DeliveryArticle article1Rem = new DeliveryArticle(100001L, 4, DeliveryArticleStatus.REMOVE);
        DeliveryArticle article2Rem = new DeliveryArticle(100002L, 6, DeliveryArticleStatus.REMOVE);
        deliveries.create(1L, new Delivery(1L, DeliveryStatus.NEW, List.of(article1, article2, article1Rem)));
        deliveries.create(1L, new Delivery(2L, DeliveryStatus.MODIFIED, List.of(article2, article1Rem, article2Rem)));
        assertThat(deliveries.getAllByBranch(1L, null)).hasSize(2);

        DeliveryProcessor processor = new DeliveryProcessor(publisher, catalog, deliveries);
        processor.run();

        List<Delivery> processed = deliveries.getAllByBranch(1L, null);
        assertThat(processed).hasSize(1);
        assertThat(processed.get(0).articles()).hasSize(1);
        assertThat(processed.get(0).articles()).allMatch(a -> a.status() == DeliveryArticleStatus.RESERVED);
        assertThat(catalog.getById(1L, 100001L).stock()).isEqualTo(5);
        assertThat(catalog.getById(1L, 100001L).reserved()).isEqualTo(3);
        assertThat(catalog.getById(1L, 100002L).stock()).isEqualTo(10);
        assertThat(catalog.getById(1L, 100002L).reserved()).isEqualTo(3);
    }

    @Test
    void testProcessWaitingAndReadyDeliveriesNotReserved() {
        DeliveryArticle article1 = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        DeliveryArticle article2 = new DeliveryArticle(100002L, 3, DeliveryArticleStatus.MODIFY);
        deliveries.create(1L, new Delivery(1L, DeliveryStatus.WAITING, List.of(article1, article2)));
        deliveries.create(1L, new Delivery(2L, DeliveryStatus.READY, List.of(article2)));
        assertThat(deliveries.getAllByStatus(DeliveryStatus.MODIFIED)).isEmpty();
        assertThat(deliveries.getAllByStatus(DeliveryStatus.WAITING)).hasSize(1);
        assertThat(deliveries.getAllByStatus(DeliveryStatus.READY)).hasSize(1);

        DeliveryProcessor processor = new DeliveryProcessor(publisher, catalog, deliveries);
        processor.run();
        assertThat(deliveries.getAllByStatus(DeliveryStatus.MODIFIED)).hasSize(2);
        assertThat(deliveries.getAllByStatus(DeliveryStatus.WAITING)).isEmpty();
        assertThat(deliveries.getAllByStatus(DeliveryStatus.READY)).isEmpty();

        List<Delivery> processed = deliveries.getAllByBranch(1L, DeliveryStatus.MODIFIED);
        assertThat(processed.get(0).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.RESERVED);
        assertThat(processed.get(0).articles().get(1).status()).isEqualTo(DeliveryArticleStatus.MODIFY);
        assertThat(processed.get(1).articles().get(0).status()).isEqualTo(DeliveryArticleStatus.MODIFY);
        assertThat(catalog.getById(1L, 100001L).stock()).isEqualTo(5);
        assertThat(catalog.getById(1L, 100001L).reserved()).isEqualTo(3);
        assertThat(catalog.getById(1L, 100002L).stock()).isEqualTo(10);
        assertThat(catalog.getById(1L, 100002L).reserved()).isEqualTo(6);
    }

    @Test
    void testProcessWaitingAndReadyDeliveriesNotInStock() {
        DeliveryArticle article1 = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        DeliveryArticle article2 = new DeliveryArticle(100002L, 12, DeliveryArticleStatus.RESERVED);
        deliveries.create(1L, new Delivery(1L, DeliveryStatus.WAITING, List.of(article1, article2)));
        deliveries.create(1L, new Delivery(2L, DeliveryStatus.READY, List.of(article2)));
        assertThat(deliveries.getAllByStatus(DeliveryStatus.WAITING)).hasSize(1);
        assertThat(deliveries.getAllByStatus(DeliveryStatus.READY)).hasSize(1);

        DeliveryProcessor processor = new DeliveryProcessor(publisher, catalog, deliveries);
        processor.run();
        assertThat(deliveries.getAllByStatus(DeliveryStatus.WAITING)).hasSize(2);
        assertThat(deliveries.getAllByStatus(DeliveryStatus.READY)).isEmpty();
    }

    @Test
    void testProcessWaitingAndReadyDeliveriesInStock() {
        DeliveryArticle article1 = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        DeliveryArticle article2 = new DeliveryArticle(100002L, 3, DeliveryArticleStatus.RESERVED);
        deliveries.create(1L, new Delivery(1L, DeliveryStatus.WAITING, List.of(article1, article2)));
        deliveries.create(1L, new Delivery(2L, DeliveryStatus.READY, List.of(article2)));
        assertThat(deliveries.getAllByStatus(DeliveryStatus.WAITING)).hasSize(1);
        assertThat(deliveries.getAllByStatus(DeliveryStatus.READY)).hasSize(1);

        DeliveryProcessor processor = new DeliveryProcessor(publisher, catalog, deliveries);
        processor.run();
        assertThat(deliveries.getAllByStatus(DeliveryStatus.WAITING)).isEmpty();
        assertThat(deliveries.getAllByStatus(DeliveryStatus.READY)).hasSize(2);
    }
}
