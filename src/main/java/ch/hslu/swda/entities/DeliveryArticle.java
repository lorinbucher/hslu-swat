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
public record DeliveryArticle(long articleId, int quantity, DeliveryArticleStatus status)
        implements Entity<DeliveryArticle> {
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
     */
    public DeliveryArticle(final Document document) {
        this(
                document.getLong("articleId"),
                document.getInteger("quantity"),
                DeliveryArticleStatus.valueOf(document.getString("status"))
        );
    }

    /**
     * Creates a MongoDB document from a delivery article.
     *
     * @return MongoDB document.
     */
    public Document toDocument() {
        return new Document()
                .append("articleId", articleId)
                .append("quantity", quantity)
                .append("status", status.name());
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
