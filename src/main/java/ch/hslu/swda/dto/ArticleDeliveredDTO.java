package ch.hslu.swda.dto;

/**
 * An article delivered message.
 *
 * @param branchId    Branch ID of the delivery.
 * @param orderNumber Order Number of the delivery.
 */
public record ArticleDeliveredDTO(long branchId, long orderNumber) {
}
