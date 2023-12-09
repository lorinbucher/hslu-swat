package ch.hslu.swda.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.lang.Nullable;

import java.math.BigDecimal;

/**
 * An article in the order message.
 *
 * @param articleId ID of the article.
 * @param name      Name of the article.
 * @param price     Price of the article.
 * @param quantity  Number of articles.
 * @param action    Order action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticleOrderDTO(long articleId, String name, BigDecimal price,
                              @Nullable Integer quantity, @Nullable String action) {
}
