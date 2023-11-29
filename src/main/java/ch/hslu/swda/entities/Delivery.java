package ch.hslu.swda.entities;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A delivery for an order of a branch.
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
