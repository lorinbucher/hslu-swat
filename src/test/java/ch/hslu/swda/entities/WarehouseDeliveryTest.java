package ch.hslu.swda.entities;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for the warehouse delivery entity.
 */
class WarehouseDeliveryTest {

    @Test
    void testNotEqualBranch() {
        final Delivery delivery = new Delivery(1L, DeliveryStatus.CHANGED, List.of());
        final WarehouseDelivery warehouseDelivery1 = new WarehouseDelivery(1L, delivery);
        final WarehouseDelivery warehouseDelivery2 = new WarehouseDelivery(2L, delivery);
        assertThat(warehouseDelivery1).isNotEqualTo(warehouseDelivery2);
    }

    @Test
    void testNotEqualDelivery() {
        final Delivery delivery1 = new Delivery(1L, DeliveryStatus.CHANGED, List.of());
        final Delivery delivery2 = new Delivery(2L, DeliveryStatus.WAITING, List.of());
        final WarehouseDelivery warehouseDelivery1 = new WarehouseDelivery(1L, delivery1);
        final WarehouseDelivery warehouseDelivery2 = new WarehouseDelivery(1L, delivery2);
        assertThat(warehouseDelivery1).isNotEqualTo(warehouseDelivery2);
    }

    @Test
    void testEqual() {
        final Delivery delivery = new Delivery(1L, DeliveryStatus.CHANGED, List.of());
        final WarehouseDelivery warehouseDelivery1 = new WarehouseDelivery(1L, delivery);
        final WarehouseDelivery warehouseDelivery2 = new WarehouseDelivery(1L, delivery);
        assertThat(warehouseDelivery1).isEqualTo(warehouseDelivery1);
        assertThat(warehouseDelivery1).isEqualTo(warehouseDelivery2);
    }

    @Test
    void testHashCodeDiffersBranch() {
        final Delivery delivery = new Delivery(1L, DeliveryStatus.CHANGED, List.of());
        final WarehouseDelivery warehouseDelivery1 = new WarehouseDelivery(1L, delivery);
        final WarehouseDelivery warehouseDelivery2 = new WarehouseDelivery(2L, delivery);
        assertThat(warehouseDelivery1).doesNotHaveSameHashCodeAs(warehouseDelivery2);
    }

    @Test
    void testHashCodeDiffersDelivery() {
        final Delivery delivery1 = new Delivery(1L, DeliveryStatus.CHANGED, List.of());
        final Delivery delivery2 = new Delivery(2L, DeliveryStatus.WAITING, List.of());
        final WarehouseDelivery warehouseDelivery1 = new WarehouseDelivery(1L, delivery1);
        final WarehouseDelivery warehouseDelivery2 = new WarehouseDelivery(1L, delivery2);
        assertThat(warehouseDelivery1).doesNotHaveSameHashCodeAs(warehouseDelivery2);
    }

    @Test
    void testHashCode() {
        final Delivery delivery = new Delivery(1L, DeliveryStatus.CHANGED, List.of());
        final WarehouseDelivery warehouseDelivery1 = new WarehouseDelivery(1L, delivery);
        final WarehouseDelivery warehouseDelivery2 = new WarehouseDelivery(1L, delivery);
        assertThat(warehouseDelivery1).hasSameHashCodeAs(warehouseDelivery2);
    }

    @Test
    void testFromDocument() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        Document document = new Document()
                .append("branchId", 1L)
                .append("orderNumber", 1L)
                .append("status", DeliveryStatus.NEW.name())
                .append("articles", Stream.of(deliveryArticle).map(DeliveryArticle::toDocument).toList());
        final WarehouseDelivery warehouseDelivery = WarehouseDelivery.fromDocument(document);
        assertThat(warehouseDelivery.branchId()).isEqualTo(1L);
        assertThat(warehouseDelivery.delivery().orderNumber()).isEqualTo(1L);
        assertThat(warehouseDelivery.delivery().status()).isEqualTo(DeliveryStatus.NEW);
        assertThat(warehouseDelivery.delivery().articles()).hasSize(1);
        assertThat(warehouseDelivery.delivery().articles().get(0)).isEqualTo(deliveryArticle);
    }

    @Test
    void testToDocument() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        final Delivery delivery = new Delivery(1L, DeliveryStatus.NEW, List.of(deliveryArticle));
        final WarehouseDelivery warehouseDelivery = new WarehouseDelivery(1L, delivery);
        Document document = WarehouseDelivery.toDocument(warehouseDelivery);
        assertThat(document.getLong("branchId")).isEqualTo(warehouseDelivery.branchId());
        assertThat(document.getLong("orderNumber")).isEqualTo(delivery.orderNumber());
        assertThat(document.getString("status")).isEqualTo(delivery.status().name());
        assertThat(document.getList("articles", Document.class)).hasSize(1);
        Document articleDocument = document.getList("articles", Document.class).get(0);
        assertThat(DeliveryArticle.fromDocument(articleDocument)).isEqualTo(deliveryArticle);
    }
}
