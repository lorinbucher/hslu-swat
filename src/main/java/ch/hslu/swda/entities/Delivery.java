package ch.hslu.swda.entities;

import org.bson.Document;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A delivery for an order.
 *
 * @param orderNumber Order Number of the delivery.
 * @param status      Status of the delivery.
 * @param articles    Articles of the delivery.
 */
public record Delivery(long orderNumber, DeliveryStatus status, List<DeliveryArticle> articles) {
    public Delivery {
        if (orderNumber < 1) {
            throw new IllegalArgumentException("orderNumber should not be lower than 1");
        }
        if (status == null) {
            throw new IllegalArgumentException("status should not be null");
        }
        if (articles == null) {
            articles = Collections.emptyList();
        }
    }

    /**
     * Creates a delivery from a MongoDB document.
     *
     * @param document MongoDB document.
     * @return Delivery.
     */
    public static Delivery fromDocument(final Document document) {
        return new Delivery(
                document.getLong("orderNumber"),
                DeliveryStatus.valueOf(document.getString("status")),
                document.getList("articles", Document.class).stream().map(DeliveryArticle::fromDocument).toList()
        );
    }

    /**
     * Creates a MongoDB document from a delivery.
     *
     * @param delivery Delivery.
     * @return MongoDB document.
     */
    public static Document toDocument(final Delivery delivery) {
        return new Document()
                .append("orderNumber", delivery.orderNumber())
                .append("status", delivery.status().name())
                .append("articles", delivery.articles().stream().map(DeliveryArticle::toDocument).toList());
    }

    /**
     * Deliveries are equal if they have the same order number.
     *
     * @param obj The delivery to compare against.
     * @return True if the order number is the same.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Delivery other
                && this.orderNumber == other.orderNumber;
    }

    /**
     * Returns the hashcode based on the order number.
     *
     * @return Hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.orderNumber);
    }
}
