package ch.hslu.swda.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * An article of the warehouse.
 *
 * @param articleId The id of the article.
 * @param name      The name of the article.
 * @param price     The price per article.
 * @param stock     The number of articles in stock.
 */
public record Article(long articleId, String name, BigDecimal price, int stock) {
    public Article {
        if (articleId < 1) {
            throw new IllegalArgumentException("articleId should not be lower than 1");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name should not be blank");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("stock should not be lower than 0");
        }
        if (price == null) {
            throw new IllegalArgumentException("price should be 0.05 or higher");
        }

        price = price.setScale(2, RoundingMode.HALF_UP);
        if (new BigDecimal("0.05").compareTo(price.setScale(2, RoundingMode.HALF_UP)) > 0) {
            throw new IllegalArgumentException("price should be 0.05 or higher");
        }
    }

    /**
     * Articles are equal if they have the same articleId.
     *
     * @param obj The article to compare against.
     * @return True if the articleId is the same.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Article other && this.articleId == other.articleId;
    }

    /**
     * Returns the hashcode based on the articleId.
     *
     * @return Hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.articleId);
    }
}
