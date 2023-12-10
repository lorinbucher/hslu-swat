package ch.hslu.swda.business;

import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the reorders db class.
 */
@Testcontainers
class ReordersDBTestIT {

    private static final String IMAGE = "mongo:4.2.24";

    private Reorders reordersDB;

    @Container
    private final GenericContainer<?> mongoContainer = new GenericContainer<>(DockerImageName.parse(IMAGE))
            .withExposedPorts(27017)
            .withStartupTimeout(Duration.ofSeconds(30))
            .waitingFor(Wait.forLogMessage(".*waiting for connections on port 27017.*\\n", 1));

    @BeforeEach
    void initializeEnv() {
        String host = mongoContainer.getHost() + ":" + mongoContainer.getMappedPort(27017);
        reordersDB = new ReordersDB(new MongoDBConnector(ReordersDB.COLLECTION, host, "", ""));
        reordersDB.create(1L, 100001L, 1);
        reordersDB.create(1L, 100002L, 2);
        reordersDB.updateStatus(1L, 2L, ReorderStatus.DELIVERED);
    }

    @Test
    void testGetByIdExisting() {
        Reorder reorder = reordersDB.getById(1L, 1L);
        assertThat(reorder).isNotNull();
        assertThat(reorder.reorderId()).isEqualTo(1L);
        assertThat(reorder.status()).isEqualTo(ReorderStatus.NEW);
        assertThat(reorder.date()).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertThat(reorder.articleId()).isEqualTo(100001L);
        assertThat(reorder.quantity()).isEqualTo(1);
    }

    @Test
    void testGetByIdNotExisting() {
        Reorder reorder = reordersDB.getById(2L, 1L);
        assertThat(reorder).isNull();
    }

    @Test
    void testGetAll() {
        List<Reorder> reorders = reordersDB.getAll(1L, null);
        assertThat(reorders).hasSize(2);
    }

    @Test
    void testGetAllEmpty() {
        List<Reorder> reorders = reordersDB.getAll(2L, null);
        assertThat(reorders).isEmpty();
    }

    @Test
    void testGetAllByStatus() {
        List<Reorder> reorders = reordersDB.getAll(1L, ReorderStatus.DELIVERED);
        assertThat(reorders).hasSize(1);
        assertThat(reorders.get(0).status()).isEqualTo(ReorderStatus.DELIVERED);
    }

    @Test
    void testCreate() {
        Reorder created = reordersDB.create(1L, 100003L, 3);
        assertThat(reordersDB.getAll(1L, null)).hasSize(3);
        assertThat(reordersDB.getById(1L, 3L)).isEqualTo(created);
        assertThat(created.status()).isEqualTo(ReorderStatus.NEW);
        assertThat(created.date()).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertThat(created.articleId()).isEqualTo(100003L);
        assertThat(created.quantity()).isEqualTo(3);
    }

    @Test
    void testCreateAutoIncrement() {
        for (int i = 3; i <= 1000; i++) {
            reordersDB.create(1L, 100000L + i, i);
        }
        assertThat(reordersDB.getAll(1L, null)).hasSize(1000);
        for (int i = 1; i <= 1000; i++) {
            Reorder reorder = reordersDB.getById(1L, i);
            assertThat(reorder).isNotNull();
            assertThat(reorder.reorderId()).isEqualTo(i);
            assertThat(reorder.articleId()).isEqualTo(100000L + i);
            assertThat(reorder.quantity()).isEqualTo(i);
        }
    }

    @Test
    void testUpdateStatusExisting() {
        Reorder updated = reordersDB.updateStatus(1L, 1L, ReorderStatus.WAITING);
        assertThat(reordersDB.getAll(1L, null)).hasSize(2);
        assertThat(reordersDB.getById(1L, 1L)).isEqualTo(updated);
        assertThat(updated.reorderId()).isEqualTo(1L);
        assertThat(updated.status()).isEqualTo(ReorderStatus.WAITING);
        assertThat(updated.date()).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        assertThat(updated.articleId()).isEqualTo(100001L);
        assertThat(updated.quantity()).isEqualTo(1L);
    }

    @Test
    void testUpdateStatusNotExisting() {
        Reorder updated = reordersDB.updateStatus(1L, 5L, ReorderStatus.COMPLETED);
        assertThat(reordersDB.getAll(1L, null)).hasSize(2);
        assertThat(reordersDB.getById(1L, 5L)).isNull();
        assertThat(updated).isNull();
    }

    @Test
    void testDeleteExisting() {
        boolean result = reordersDB.delete(1L, 1L);
        assertThat(result).isTrue();
        assertThat(reordersDB.getAll(1L, null)).hasSize(1);
        assertThat(reordersDB.getById(1L, 1L)).isNull();
    }

    @Test
    void testDeleteNotExisting() {
        boolean result = reordersDB.delete(1L, 5L);
        assertThat(result).isFalse();
        assertThat(reordersDB.getAll(1L, null)).hasSize(2);
    }
}
