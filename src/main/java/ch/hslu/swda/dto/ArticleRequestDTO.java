package ch.hslu.swda.dto;

import java.util.Collections;
import java.util.List;

/**
 * An article request message.
 *
 * @param orderNumber Order Number of the request.
 * @param branchId    Branch ID of the request.
 * @param articles    List of articles of the request.
 */
public record ArticleRequestDTO(long orderNumber, long branchId, List<Long> articles) {
    public ArticleRequestDTO {
        if (orderNumber < 1) {
            throw new IllegalArgumentException("orderNumber should not be lower than 1");
        }
        if (branchId < 1) {
            throw new IllegalArgumentException("branchId should not be lower than 1");
        }
        if (articles == null) {
            articles = Collections.emptyList();
        }
    }
}
