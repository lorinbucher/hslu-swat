package ch.hslu.swda.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * An article from the catalog of a branch.
 *
 * @param articleId ID of the article.
 * @param name      Name of the article.
 * @param price     Price per article.
 * @param minStock  Minimum number of articles in stock.
 * @param stock     Number of articles in stock.
 */
public record Article(long articleId, String name, BigDecimal price, int minStock, int stock) {
    public Article {
        if (articleId < 100000) {
            throw new IllegalArgumentException("articleId should not be lower than 100000");
        }
        if (articleId > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("articleId should not be higher than " + Integer.MAX_VALUE);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name should not be blank");
        }
        if (minStock < 0) {
            throw new IllegalArgumentException("minStock should not be lower than 0");
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
     * Articles are equal if they have the same article ID.
     *
     * @param obj The article to compare against.
     * @return True if the article ID is the same.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Article other
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
