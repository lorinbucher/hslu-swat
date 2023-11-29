package ch.hslu.swda.entities;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the warehouse reorder entity.
 */
class WarehouseReorderTest {

    @Test
    void testNotEqualBranch() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100001L, 5);
        final WarehouseReorder warehouseReorder1 = new WarehouseReorder(1L, reorder);
        final WarehouseReorder warehouseReorder2 = new WarehouseReorder(2L, reorder);
        assertThat(warehouseReorder1).isNotEqualTo(warehouseReorder2);
    }

    @Test
    void testNotEqualReorder() {
        final Reorder reorder1 = new Reorder(1L, ReorderStatus.NEW, "", 100001L, 5);
        final Reorder reorder2 = new Reorder(2L, ReorderStatus.WAITING, "", 100002L, 9);
        final WarehouseReorder warehouseReorder1 = new WarehouseReorder(1L, reorder1);
        final WarehouseReorder warehouseReorder2 = new WarehouseReorder(1L, reorder2);
        assertThat(warehouseReorder1).isNotEqualTo(warehouseReorder2);
    }

    @Test
    void testEqual() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100001L, 5);
        final WarehouseReorder warehouseReorder1 = new WarehouseReorder(1L, reorder);
        final WarehouseReorder warehouseReorder2 = new WarehouseReorder(1L, reorder);
        assertThat(warehouseReorder1).isEqualTo(warehouseReorder1);
        assertThat(warehouseReorder1).isEqualTo(warehouseReorder2);
    }

    @Test
    void testHashCodeDiffersBranch() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100001L, 5);
        final WarehouseReorder warehouseReorder1 = new WarehouseReorder(1L, reorder);
        final WarehouseReorder warehouseReorder2 = new WarehouseReorder(2L, reorder);
        assertThat(warehouseReorder1).doesNotHaveSameHashCodeAs(warehouseReorder2);
    }

    @Test
    void testHashCodeDiffersReorder() {
        final Reorder reorder1 = new Reorder(1L, ReorderStatus.NEW, "", 100001L, 5);
        final Reorder reorder2 = new Reorder(2L, ReorderStatus.WAITING, "", 100002L, 9);
        final WarehouseReorder warehouseReorder1 = new WarehouseReorder(1L, reorder1);
        final WarehouseReorder warehouseReorder2 = new WarehouseReorder(1L, reorder2);
        assertThat(warehouseReorder1).doesNotHaveSameHashCodeAs(warehouseReorder2);
    }

    @Test
    void testHashCode() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100001L, 5);
        final WarehouseReorder warehouseReorder1 = new WarehouseReorder(1L, reorder);
        final WarehouseReorder warehouseReorder2 = new WarehouseReorder(1L, reorder);
        assertThat(warehouseReorder1).hasSameHashCodeAs(warehouseReorder2);
    }

    @Test
    void testFromDocument() {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Document document = new Document()
                .append("branchId", 1L)
                .append("reorderId", 1L)
                .append("status", ReorderStatus.NEW.name())
                .append("date", new Date())
                .append("articleId", 100001L)
                .append("quantity", 5);
        final WarehouseReorder warehouseReorder = WarehouseReorder.fromDocument(document);
        assertThat(warehouseReorder.branchId()).isEqualTo(1L);
        assertThat(warehouseReorder.reorder().reorderId()).isEqualTo(1L);
        assertThat(warehouseReorder.reorder().status()).isEqualTo(ReorderStatus.NEW);
        assertThat(warehouseReorder.reorder().date()).isEqualTo(date);
        assertThat(warehouseReorder.reorder().articleId()).isEqualTo(100001L);
        assertThat(warehouseReorder.reorder().quantity()).isEqualTo(5);
    }

    @Test
    void testToDocument() {
        final Reorder reorder = new Reorder(1L, ReorderStatus.NEW, "", 100001L, 5);
        final WarehouseReorder warehouseReorder = new WarehouseReorder(1L, reorder);
        Document document = WarehouseReorder.toDocument(warehouseReorder);
        assertThat(document.getLong("branchId")).isEqualTo(warehouseReorder.branchId());
        assertThat(document.getLong("reorderId")).isEqualTo(reorder.reorderId());
        assertThat(document.getString("status")).isEqualTo(reorder.status().name());
        assertThat(document.getDate("date")).isEqualTo(reorder.date());
        assertThat(document.getLong("articleId")).isEqualTo(reorder.articleId());
        assertThat(document.getInteger("quantity")).isEqualTo(reorder.quantity());
    }
}
