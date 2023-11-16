package ch.hslu.swda.dto;

import java.util.List;

/**
 * An article request message.
 *
 * @param orderNumber Order Number of the request.
 * @param branchId    Branch ID of the request.
 * @param articles    List of articles of the request.
 */
public record ArticleRequestDTO(long orderNumber, long branchId, List<Long> articles) {
}
