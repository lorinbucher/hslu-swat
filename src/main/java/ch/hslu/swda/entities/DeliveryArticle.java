package ch.hslu.swda.entities;

import org.bson.Document;

import java.util.Objects;

/**
 * An article of a delivery.
 *
 * @param articleId ID of the article.
 * @param quantity  Number of articles ordered.
 * @param status    Status of the article in the warehouse.
 */
public record DeliveryArticle(long articleId, int quantity, DeliveryArticleStatus status) {
    public DeliveryArticle {
        if (articleId < 100000) {
            throw new IllegalArgumentException("articleId should not be lower than 100000");
        }
        if (articleId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("articleId should not be higher than " + Integer.MAX_VALUE);
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity should not be lower than 1");
        }
        if (status == null) {
            throw new IllegalArgumentException("status should not be null");
        }
    }

    /**
     * Creates a delivery article from a MongoDB document.
     *
     * @param document MongoDB document.
     * @return Delivery article.
     */
    public static DeliveryArticle fromDocument(final Document document) {
        return new DeliveryArticle(
                document.getLong("articleId"),
                document.getInteger("quantity"),
                DeliveryArticleStatus.valueOf(document.getString("status"))
        );
    }

    /**
     * Creates a MongoDB document from a delivery article.
     *
     * @param deliveryArticle Delivery article.
     * @return MongoDB document.
     */
    public static Document toDocument(final DeliveryArticle deliveryArticle) {
        return new Document()
                .append("articleId", deliveryArticle.articleId())
                .append("quantity", deliveryArticle.quantity())
                .append("status", deliveryArticle.status().name());
    }

    /**
     * Delivery articles are equal if they have the same article ID.
     *
     * @param obj The delivery article to compare against.
     * @return True if the article ID is the same.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof DeliveryArticle other
                && this.articleId == other.articleId;
    }

    /**
     * Returns the hashcode based on the article ID.
     *
     * @return Hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.articleId);
    }
}
