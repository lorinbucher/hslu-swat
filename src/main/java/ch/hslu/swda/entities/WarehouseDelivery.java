package ch.hslu.swda.entities;

import org.bson.Document;

/**
 * A delivery from the warehouse.
 *
 * @param branchId ID of the branch.
 * @param delivery Delivery.
 */
public record WarehouseDelivery(long branchId, Delivery delivery) {

    /**
     * Creates a warehouse delivery from a MongoDB document.
     *
     * @param document MongoDB document.
     * @return Warehouse delivery.
     */
    public static WarehouseDelivery fromDocument(final Document document) {
        return new WarehouseDelivery(
                document.getLong("branchId"),
                new Delivery(
                        document.getLong("orderNumber"),
                        DeliveryStatus.valueOf(document.getString("status")),
                        document.getList("articles", Document.class).stream()
                                .map(DeliveryArticle::fromDocument).toList()
                )
        );
    }

    /**
     * Creates a MongoDB document from a warehouse delivery.
     *
     * @param warehouseDelivery Warehouse delivery.
     * @return MongoDB document.
     */
    public static Document toDocument(final WarehouseDelivery warehouseDelivery) {
        return new Document()
                .append("branchId", warehouseDelivery.branchId)
                .append("orderNumber", warehouseDelivery.delivery.orderNumber())
                .append("status", warehouseDelivery.delivery.status().name())
                .append("articles", warehouseDelivery.delivery.articles().stream().map(DeliveryArticle::toDocument).toList());
    }
}
