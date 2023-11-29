package ch.hslu.swda.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the delivery entity.
 */
public class DeliveryTest {

    @Test
    void testOrderNumberInvalid() {
        assertThatThrownBy(() -> new Delivery(0L, DeliveryStatus.NEW, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("orderNumber should not be lower than 1");
    }

    @Test
    void testOrderNumberValid() {
        final Delivery delivery = new Delivery(1L, DeliveryStatus.NEW, List.of());
        assertThat(delivery.orderNumber()).isEqualTo(1L);
    }

    @Test
    void testStatusInvalid() {
        assertThatThrownBy(() -> new Delivery(1L, null, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("status should not be null");
    }

    @Test
    void testStatusValid() {
        final Delivery delivery = new Delivery(1L, DeliveryStatus.NEW, List.of());
        assertThat(delivery.status()).isEqualTo(DeliveryStatus.NEW);
    }

    @Test
    void testArticlesNull() {
        final Delivery delivery = new Delivery(1L, DeliveryStatus.NEW, null);
        assertThat(delivery.articles()).isEmpty();
    }

    @Test
    void testArticlesValid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        final Delivery delivery = new Delivery(1L, DeliveryStatus.NEW, List.of(deliveryArticle));
        assertThat(delivery.articles()).hasSize(1);
        assertThat(delivery.articles().get(0)).isEqualTo(deliveryArticle);
    }

    @Test
    void testNotEqual() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        final Delivery delivery1 = new Delivery(1L, DeliveryStatus.READY, List.of(deliveryArticle));
        final Delivery delivery2 = new Delivery(2L, DeliveryStatus.COMPLETED, List.of(deliveryArticle));
        assertThat(delivery1).isNotEqualTo(delivery2);
    }

    @Test
    void testEqual() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        final Delivery delivery1 = new Delivery(1L, DeliveryStatus.CHANGED, List.of(deliveryArticle));
        final Delivery delivery2 = new Delivery(1L, DeliveryStatus.NEW, List.of(deliveryArticle, deliveryArticle));
        assertThat(delivery1).isEqualTo(delivery1);
        assertThat(delivery1).isEqualTo(delivery2);
    }

    @Test
    void testHashCodeDiffers() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        final Delivery delivery1 = new Delivery(1L, DeliveryStatus.READY, List.of(deliveryArticle));
        final Delivery delivery2 = new Delivery(2L, DeliveryStatus.COMPLETED, List.of(deliveryArticle));
        assertThat(delivery1).doesNotHaveSameHashCodeAs(delivery2);
    }

    @Test
    void testHashCode() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        final Delivery delivery1 = new Delivery(1L, DeliveryStatus.CHANGED, List.of(deliveryArticle));
        final Delivery delivery2 = new Delivery(1L, DeliveryStatus.NEW, List.of(deliveryArticle, deliveryArticle));
        assertThat(delivery1).hasSameHashCodeAs(delivery2);
    }

    @Test
    void testJsonObject() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, DeliveryArticleStatus.RESERVED);
        final Delivery delivery = new Delivery(1L, DeliveryStatus.WAITING, List.of(deliveryArticle));
        String start = "{\"orderNumber\":1,\"status\":\"WAITING\",\"articles\":[{";
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
