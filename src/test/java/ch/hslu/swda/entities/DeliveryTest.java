package ch.hslu.swda.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the delivery entity.
 */
public class DeliveryTest {

    @Test
    void testFromDocument() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        Document document = new Document()
                .append("orderNumber", 1L)
                .append("status", "processing")
                .append("articles", Stream.of(deliveryArticle).map(DeliveryArticle::toDocument).toList());
        final Delivery delivery = Delivery.fromDocument(document);
        assertThat(delivery.orderNumber()).isEqualTo(1L);
        assertThat(delivery.status()).isEqualTo("processing");
        assertThat(delivery.articles()).hasSize(1);
        assertThat(delivery.articles().get(0)).isEqualTo(deliveryArticle);
    }

    @Test
    void testToDocument() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery = new Delivery(1L, "processing", List.of(deliveryArticle));
        Document document = Delivery.toDocument(delivery);
        assertThat(document.getLong("orderNumber")).isEqualTo(delivery.orderNumber());
        assertThat(document.getString("status")).isEqualTo(delivery.status());
        assertThat(document.getList("articles", Document.class)).hasSize(1);
        Document articleDocument = document.getList("articles", Document.class).get(0);
        assertThat(DeliveryArticle.fromDocument(articleDocument)).isEqualTo(deliveryArticle);
    }

    @Test
    void testOrderNumberInvalid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        assertThatThrownBy(() -> new Delivery(0L, "processing", List.of(deliveryArticle)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("orderNumber should not be lower than 1");
    }

    @Test
    void testOrderNumberValid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery = new Delivery(1L, "processing", List.of(deliveryArticle));
        assertThat(delivery.orderNumber()).isEqualTo(1L);
    }

    @Test
    void testStatusInvalid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        assertThatThrownBy(() -> new Delivery(1L, "", List.of(deliveryArticle)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("status should not be blank");
    }

    @Test
    void testStatusValid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery = new Delivery(1L, "processing", List.of(deliveryArticle));
        assertThat(delivery.status()).isEqualTo("processing");
    }

    @Test
    void testArticlesInvalid() {
        assertThatThrownBy(() -> new Delivery(1L, "processing", List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articles should not be empty");
    }

    @Test
    void testArticlesValid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery = new Delivery(1L, "processing", List.of(deliveryArticle));
        assertThat(delivery.articles()).hasSize(1);
        assertThat(delivery.articles().get(0)).isEqualTo(deliveryArticle);
    }

    @Test
    void testNotEqual() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery1 = new Delivery(1L, "processing", List.of(deliveryArticle));
        final Delivery delivery2 = new Delivery(2L, "processing", List.of(deliveryArticle));
        assertThat(delivery1).isNotEqualTo(delivery2);
    }

    @Test
    void testEqual() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery1 = new Delivery(1L, "processing", List.of(deliveryArticle));
        final Delivery delivery2 = new Delivery(1L, "completed", List.of(deliveryArticle, deliveryArticle));
        assertThat(delivery1).isEqualTo(delivery1);
        assertThat(delivery1).isEqualTo(delivery2);
    }

    @Test
    void testHashCodeDiffers() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery1 = new Delivery(1L, "processing", List.of(deliveryArticle));
        final Delivery delivery2 = new Delivery(2L, "processing", List.of(deliveryArticle));
        assertThat(delivery1).doesNotHaveSameHashCodeAs(delivery2);
    }

    @Test
    void testHashCode() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery1 = new Delivery(1L, "processing", List.of(deliveryArticle));
        final Delivery delivery2 = new Delivery(1L, "completed", List.of(deliveryArticle, deliveryArticle));
        assertThat(delivery1).hasSameHashCodeAs(delivery2);
    }

    @Test
    void testJsonObject() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        final Delivery delivery = new Delivery(1L, "processing", List.of(deliveryArticle));
        String start = "{\"orderNumber\":1,\"status\":\"processing\",\"articles\":[{";
        String end = "}]}";
        try {
            String json = new ObjectMapper().writeValueAsString(delivery);
            assertThat(json).startsWith(start);
            assertThat(json).endsWith(end);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
