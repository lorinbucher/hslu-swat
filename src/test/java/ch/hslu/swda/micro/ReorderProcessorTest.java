package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogMemory;
import ch.hslu.swda.business.Reorders;
import ch.hslu.swda.business.ReordersMemory;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.Article;
import ch.hslu.swda.entities.ReorderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the reorder processor.
 */
class ReorderProcessorTest {

    private MessagePublisherDummy<LogEventDTO> publisher;
    private ProductCatalog catalog;
    private Reorders reorders;

    @BeforeEach
    void initializeEnv() {
        publisher = new MessagePublisherDummy<>();
        catalog = new ProductCatalogMemory();
        catalog.create(1, new Article(100001L, "Article 1", new BigDecimal("5.25"), 3, 5, 0));
        catalog.create(1, new Article(100002L, "Article 2", new BigDecimal("9.95"), 5, 7, 0));
        reorders = new ReordersMemory();
    }

    @Test
    void testReorderArticlesWithLowStockNone() {
        ReorderProcessor processor = new ReorderProcessor(publisher, catalog, reorders);
        processor.run();
        assertThat(reorders.getAllByBranch(1L, null)).isEmpty();
    }

    @Test
    void testReorderArticlesWithLowStock() {
        catalog.changeStock(1L, 100001L, -3);
        catalog.changeReserved(1L, 100002L, 10);

        ReorderProcessor processor = new ReorderProcessor(publisher, catalog, reorders);
        processor.run();
        assertThat(reorders.getAllByBranch(1L, null)).hasSize(2);
        assertThat(reorders.getById(1L, 1L).articleId()).isEqualTo(100001L);
        assertThat(reorders.getById(1L, 1L).quantity()).isEqualTo(4);
        assertThat(reorders.getById(1L, 2L).articleId()).isEqualTo(100002L);
        assertThat(reorders.getById(1L, 2L).quantity()).isEqualTo(13);
    }

    @Test
    void testReorderArticlesWithLowStockAndReorders() {
        catalog.changeStock(1L, 100001L, -3);
        catalog.changeReserved(1L, 100002L, 10);

        ReorderProcessor processor = new ReorderProcessor(publisher, catalog, reorders);
        processor.run();
        assertThat(reorders.getAllByBranch(1L, null)).hasSize(2);
        catalog.changeReserved(1L, 100001L, 3);
        catalog.changeStock(1L, 100002L, -6);
        processor.run();
        assertThat(reorders.getAllByBranch(1L, null)).hasSize(3);
        assertThat(reorders.getById(1L, 3L).articleId()).isEqualTo(100002L);
        assertThat(reorders.getById(1L, 3L).quantity()).isEqualTo(6);
    }

    @Test
    void testProcessDeliveredReorders() {
        reorders.create(1L, 100001L, 5);
        reorders.create(1L, 100002L, 10);
        reorders.create(1L, 100002L, 15);
        reorders.updateStatus(1L, 1L, ReorderStatus.DELIVERED);
        reorders.updateStatus(1L, 2L, ReorderStatus.DELIVERED);
        assertThat(reorders.getAllByStatus(ReorderStatus.DELIVERED)).hasSize(2);

        ReorderProcessor processor = new ReorderProcessor(publisher, catalog, reorders);
        processor.run();
        assertThat(catalog.getById(1L, 100001L).stock()).isEqualTo(10);
        assertThat(catalog.getById(1L, 100002L).stock()).isEqualTo(17);
        assertThat(reorders.getAllByStatus(ReorderStatus.DELIVERED)).isEmpty();
        assertThat(reorders.getAllByStatus(ReorderStatus.COMPLETED)).hasSize(2);
    }

    @Test
    void testProcessNewReorders() {
        reorders.create(1L, 100001L, 5);
        reorders.create(1L, 100002L, 10);
        assertThat(reorders.getAllByStatus(ReorderStatus.NEW)).hasSize(2);

        ReorderProcessor processor = new ReorderProcessor(publisher, catalog, reorders);
        processor.run();
        assertThat(reorders.getAllByStatus(ReorderStatus.NEW)).isEmpty();
        assertThat(reorders.getAllByStatus(ReorderStatus.WAITING)).hasSize(2);
        assertThat(reorders.getById(1L, 1L).quantity()).isEqualTo(5);
        assertThat(reorders.getById(1L, 2L).quantity()).isEqualTo(10);
    }

    @Test
    void testProcessNewReordersBig() {
        reorders.create(1L, 100001L, 1000);
        reorders.create(1L, 100001L, 5);
        assertThat(reorders.getAllByStatus(ReorderStatus.NEW)).hasSize(2);

        ReorderProcessor processor = new ReorderProcessor(publisher, catalog, reorders);
        processor.run();
        assertThat(reorders.getAllByStatus(ReorderStatus.NEW)).hasSize(1);
        assertThat(reorders.getAllByStatus(ReorderStatus.WAITING)).hasSize(1);
        assertThat(reorders.getById(1L, 1L).quantity()).isBetween(1, 1000);
    }
}
