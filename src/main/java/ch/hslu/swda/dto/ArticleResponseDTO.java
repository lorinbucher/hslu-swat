package ch.hslu.swda.dto;

import java.util.List;

/**
 * An article response message.
 *
 * @param orderNumber Order Number of the request.
 * @param branchId    Branch ID of the request.
 * @param articles    List of articles of the request.
 * @param error       List of errors.
 */
public record ArticleResponseDTO(long orderNumber, long branchId, List<ArticleOrderDTO> articles, List<String> error) {
}
