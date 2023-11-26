package ch.hslu.swda.dto;

import java.math.BigDecimal;

/**
 * An article in the response message.
 *
 * @param articleId ID of the article.
 * @param name      Name of the article.
 * @param price     Price of the article.
 * @param quantity  Number of articles.
 */
public record ArticleOrderDTO(long articleId, String name, BigDecimal price, int quantity) {
}
