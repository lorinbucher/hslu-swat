package ch.hslu.swda.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for the delivery article entity.
 */
public class DeliveryArticleTest {

    @Test
    void testFromDocument() {
        Document document = new Document()
                .append("articleId", 100005L)
                .append("quantity", 2)
                .append("status", "inStock");
        final DeliveryArticle deliveryArticle = DeliveryArticle.fromDocument(document);
        assertThat(deliveryArticle.articleId()).isEqualTo(100005L);
        assertThat(deliveryArticle.quantity()).isEqualTo(2);
        assertThat(deliveryArticle.status()).isEqualTo("inStock");
    }

    @Test
    void testToDocument() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100005L, 2, "inStock");
        Document document = DeliveryArticle.toDocument(deliveryArticle);
        assertThat(document.getLong("articleId")).isEqualTo(deliveryArticle.articleId());
        assertThat(document.getInteger("quantity")).isEqualTo(deliveryArticle.quantity());
        assertThat(document.getString("status")).isEqualTo(deliveryArticle.status());
    }

    @Test
    void testArticleIdInvalidMin() {
        assertThatThrownBy(() -> new DeliveryArticle(99999L, 2, "inStock"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be lower than 100000");
    }

    @Test
    void testArticleIdInvalidMax() {
        assertThatThrownBy(() -> new DeliveryArticle(Integer.MAX_VALUE + 1L, 2, "inStock"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("articleId should not be higher than " + Integer.MAX_VALUE);
    }

    @Test
    void testArticleIdValid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100000L, 2, "inStock");
        assertThat(deliveryArticle.articleId()).isEqualTo(100000L);
    }

    @Test
    void testQuantityInvalid() {
        assertThatThrownBy(() -> new DeliveryArticle(100001L, 0, "inStock"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("quantity should not be lower than 1");
    }

    @Test
    void testQuantityValid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 1, "inStock");
        assertThat(deliveryArticle.quantity()).isEqualTo(1);
    }

    @Test
    void testStatusInvalid() {
        assertThatThrownBy(() -> new DeliveryArticle(100001L, 2, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("status should not be blank");
    }

    @Test
    void testStatusValid() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        assertThat(deliveryArticle.status()).isEqualTo("inStock");
    }

    @Test
    void testNotEqual() {
        final DeliveryArticle deliveryArticle1 = new DeliveryArticle(100001L, 2, "inStock");
        final DeliveryArticle deliveryArticle2 = new DeliveryArticle(100002L, 2, "inStock");
        assertThat(deliveryArticle1).isNotEqualTo(deliveryArticle2);
    }

    @Test
    void testEqual() {
        final DeliveryArticle deliveryArticle1 = new DeliveryArticle(100001L, 2, "inStock");
        final DeliveryArticle deliveryArticle2 = new DeliveryArticle(100001L, 3, "ordered");
        assertThat(deliveryArticle1).isEqualTo(deliveryArticle1);
        assertThat(deliveryArticle1).isEqualTo(deliveryArticle2);
    }

    @Test
    void testHashCodeDiffers() {
        final DeliveryArticle deliveryArticle1 = new DeliveryArticle(100001L, 2, "inStock");
        final DeliveryArticle deliveryArticle2 = new DeliveryArticle(100002L, 2, "inStock");
        assertThat(deliveryArticle1).doesNotHaveSameHashCodeAs(deliveryArticle2);
    }

    @Test
    void testHashCode() {
        final DeliveryArticle deliveryArticle1 = new DeliveryArticle(100001L, 2, "inStock");
        final DeliveryArticle deliveryArticle2 = new DeliveryArticle(100001L, 3, "ordered");
        assertThat(deliveryArticle1).hasSameHashCodeAs(deliveryArticle2);
    }

    @Test
    void testJsonObject() {
        final DeliveryArticle deliveryArticle = new DeliveryArticle(100001L, 2, "inStock");
        String deliveryArticleJson = "{\"articleId\":100001,\"quantity\":2,\"status\":\"inStock\"}";
        try {
            assertThat(new ObjectMapper().writeValueAsString(deliveryArticle)).isEqualTo(deliveryArticleJson);
        } catch (JsonProcessingException e) {
            assertThat(e).isNull();
        }
    }
}
