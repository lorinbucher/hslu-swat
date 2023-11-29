package ch.hslu.swda.dto;

import java.util.Collections;
import java.util.List;

/**
 * An order message.
 *
 * @param branchId    Branch ID of the request.
 * @param orderNumber Order Number of the request.
 * @param articles    List of articles of the request.
 * @param error       List of errors.
 */
public record OrderDTO(long branchId, long orderNumber, List<ArticleOrderDTO> articles, List<String> error) {
    public OrderDTO {
        if (branchId < 1) {
            throw new IllegalArgumentException("branchId should not be lower than 1");
        }
        if (orderNumber < 1) {
            throw new IllegalArgumentException("orderNumber should not be lower than 1");
        }
        if (articles == null) {
            articles = Collections.emptyList();
        }
    }
}
