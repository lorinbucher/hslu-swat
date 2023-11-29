package ch.hslu.swda.entities;

import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A reorder in the warehouse.
 *
 * @param branchId ID of the branch.
 * @param reorder  Reorder.
 */
public record WarehouseReorder(long branchId, Reorder reorder) {

    /**
     * Creates a warehouse reorder from a MongoDB document.
     *
     * @param document MongoDB document.
     * @return Warehouse reorder.
     */
    public static WarehouseReorder fromDocument(final Document document) {
        return new WarehouseReorder(
                document.getLong("branchId"),
                new Reorder(
                        document.getLong("reorderId"),
                        ReorderStatus.valueOf(document.getString("status")),
                        new SimpleDateFormat("yyyy-MM-dd").format(document.getDate("date")),
                        document.getLong("articleId"),
                        document.getInteger("quantity")
                )
        );
    }

    /**
     * Creates a MongoDB document from a warehouse reorder.
     *
     * @param warehouseReorder Warehouse reorder.
     * @return MongoDB document.
     */
    public static Document toDocument(final WarehouseReorder warehouseReorder) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(warehouseReorder.reorder.date());
        } catch (ParseException e) {
            date = new Date();
        }
        return new Document()
                .append("branchId", warehouseReorder.branchId)
                .append("reorderId", warehouseReorder.reorder.reorderId())
                .append("status", warehouseReorder.reorder.status().name())
                .append("date", date)
                .append("articleId", warehouseReorder.reorder.articleId())
                .append("quantity", warehouseReorder.reorder.quantity());
    }
}
