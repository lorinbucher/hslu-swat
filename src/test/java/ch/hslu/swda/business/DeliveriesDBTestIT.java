package ch.hslu.swda.business;

import ch.hslu.swda.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the deliveries db class.
 */
@Testcontainers
class DeliveriesDBTestIT {

    private static final String IMAGE = "mongo:4.2.24";

    private Deliveries deliveriesDB;
    private List<DeliveryArticle> articles;

    @Container
    private final GenericContainer<?> mongoContainer = new GenericContainer<>(DockerImageName.parse(IMAGE))
            .withExposedPorts(27017)
            .withStartupTimeout(Duration.ofSeconds(30))
            .waitingFor(Wait.forLogMessage(".*waiting for connections on port 27017.*\\n", 1));

    @BeforeEach
    void initializeEnv() {
        DeliveryArticle article1 = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        DeliveryArticle article2 = new DeliveryArticle(100002L, 4, DeliveryArticleStatus.RESERVED);
        articles = List.of(article1, article2);

        String host = mongoContainer.getHost() + ":" + mongoContainer.getMappedPort(27017);
        deliveriesDB = new DeliveriesDB(new MongoDBConnector(DeliveriesDB.COLLECTION, host, "", ""));
        Delivery delivery1 = new Delivery(1L, DeliveryStatus.NEW, articles);
        Delivery delivery2 = new Delivery(2L, DeliveryStatus.COMPLETED, articles);
        deliveriesDB.create(1L, delivery1);
        deliveriesDB.create(1L, delivery2);
    }

    @Test
    void testGetByIdExisting() {
        Delivery existing = new Delivery(1L, DeliveryStatus.NEW, articles);
        Delivery delivery = deliveriesDB.getById(1L, 1L);
        assertThat(delivery).isNotNull();
        assertThat(delivery).isEqualTo(existing);
        assertThat(delivery.status()).isEqualTo(existing.status());
        assertThat(delivery.articles()).hasSize(2);
    }

    @Test
    void testGetByIdNotExisting() {
        Delivery delivery = deliveriesDB.getById(2L, 1L);
        assertThat(delivery).isNull();
    }

    @Test
    void testGetAllByBranch() {
        List<Delivery> deliveries = deliveriesDB.getAllByBranch(1L, null);
        assertThat(deliveries).hasSize(2);
    }

    @Test
    void testGetAllByBranchEmpty() {
        List<Delivery> deliveries = deliveriesDB.getAllByBranch(2L, null);
        assertThat(deliveries).isEmpty();
    }

    @Test
    void testGetAllByBranchWithStatus() {
        List<Delivery> deliveries = deliveriesDB.getAllByBranch(1L, DeliveryStatus.COMPLETED);
        assertThat(deliveries).hasSize(1);
        assertThat(deliveries.get(0).status()).isEqualTo(DeliveryStatus.COMPLETED);
    }

    @Test
    void testGetAllByStatus() {
        deliveriesDB.create(2L, new Delivery(5L, DeliveryStatus.NEW, List.of()));
        deliveriesDB.updateStatus(1L, 1L, DeliveryStatus.WAITING);
        deliveriesDB.updateStatus(2L, 5L, DeliveryStatus.WAITING);
        List<WarehouseEntity<Delivery>> deliveries = deliveriesDB.getAllByStatus(DeliveryStatus.WAITING);
        assertThat(deliveries).hasSize(2);
        assertThat(deliveries.get(0).branchId()).isEqualTo(1L);
        assertThat(((Delivery) deliveries.get(0).entity()).status()).isEqualTo(DeliveryStatus.WAITING);
        assertThat(deliveries.get(1).branchId()).isEqualTo(2L);
        assertThat(((Delivery) deliveries.get(1).entity()).status()).isEqualTo(DeliveryStatus.WAITING);
    }

    @Test
    void testGetAllByStatusEmpty() {
        List<WarehouseEntity<Delivery>> deliveries = deliveriesDB.getAllByStatus(DeliveryStatus.WAITING);
        assertThat(deliveries).hasSize(0);
    }

    @Test
    void testCreateExisting() {
        Delivery delivery = new Delivery(1L, DeliveryStatus.WAITING, List.of(articles.get(0)));
        Delivery created = deliveriesDB.create(1L, delivery);
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(2);
        assertThat(created).isEqualTo(delivery);
        assertThat(created.status()).isNotEqualTo(delivery.status());
        assertThat(created.articles()).isNotEqualTo(delivery.articles());
    }

    @Test
    void testCreateNotExisting() {
        Delivery delivery = new Delivery(5L, DeliveryStatus.NEW, articles);
        Delivery created = deliveriesDB.create(1L, delivery);
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(3);
        assertThat(deliveriesDB.getById(1L, 5L)).isEqualTo(delivery);
        assertThat(created).isEqualTo(delivery);
        assertThat(created.status()).isEqualTo(delivery.status());
        assertThat(created.articles()).isEqualTo(delivery.articles());
    }

    @Test
    void testUpdateExisting() {
        Delivery delivery = new Delivery(1L, DeliveryStatus.DELIVERED, List.of(articles.get(1)));
        Delivery updated = deliveriesDB.update(1L, 1L, delivery);
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveriesDB.getById(1L, 1L)).isEqualTo(delivery);
        assertThat(updated).isEqualTo(delivery);
        assertThat(updated.status()).isEqualTo(delivery.status());
        assertThat(updated.articles()).isEqualTo(delivery.articles());
    }

    @Test
    void testUpdateExistingIdMismatch() {
        Delivery delivery = new Delivery(5L, DeliveryStatus.WAITING, List.of(articles.get(1)));
        Delivery updated = deliveriesDB.update(1L, 1L, delivery);
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(2);
        assertThat(updated.orderNumber()).isEqualTo(1L);
        assertThat(updated.status()).isEqualTo(delivery.status());
        assertThat(updated.articles()).isEqualTo(delivery.articles());
    }

    @Test
    void testUpdateNotExisting() {
        Delivery delivery = new Delivery(5L, DeliveryStatus.WAITING, List.of(articles.get(1)));
        Delivery updated = deliveriesDB.update(1L, 5L, delivery);
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveriesDB.getById(1L, 5L)).isNull();
        assertThat(updated).isNull();
    }

    @Test
    void testUpdateStatusExisting() {
        Delivery updated = deliveriesDB.updateStatus(1L, 1L, DeliveryStatus.DELIVERED);
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveriesDB.getById(1L, 1L)).isEqualTo(updated);
        assertThat(deliveriesDB.getById(1L, 1L).status()).isEqualTo(DeliveryStatus.DELIVERED);
    }

    @Test
    void testUpdateStatusNotExisting() {
        Delivery updated = deliveriesDB.updateStatus(1L, 5L, DeliveryStatus.DELIVERED);
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(2);
        assertThat(deliveriesDB.getById(1L, 5L)).isNull();
        assertThat(updated).isNull();
    }

    @Test
    void testDeleteExisting() {
        boolean result = deliveriesDB.delete(1L, 1L);
        assertThat(result).isTrue();
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(1);
        assertThat(deliveriesDB.getById(1L, 1L)).isNull();
    }

    @Test
    void testDeleteNotExisting() {
        boolean result = deliveriesDB.delete(1L, 5L);
        assertThat(result).isFalse();
        assertThat(deliveriesDB.getAllByBranch(1L, null)).hasSize(2);
    }
}
