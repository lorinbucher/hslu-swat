package ch.hslu.swda.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An article of the warehouse.
 *
 * @param articleId The id of the article.
 * @param name      The name of the article.
 * @param price     The price per article.
 * @param stock     The number of articles in stock.
 */
public record Article(Integer articleId, String name, BigDecimal price, Integer stock) {
    public Article {
        if (articleId == null || articleId < 1) {
            throw new IllegalArgumentException("articleId should not be lower than 1");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name should not be blank");
        }
        if (stock == null || stock < 0) {
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
}
