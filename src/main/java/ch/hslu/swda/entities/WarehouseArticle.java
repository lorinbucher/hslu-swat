package ch.hslu.swda.entities;

import org.bson.Document;

import java.math.BigDecimal;

/**
 * An article in the warehouse.
 *
 * @param branchId ID of the branch.
 * @param article  Article.
 */
public record WarehouseArticle(long branchId, Article article) {

    /**
     * Creates a warehouse article from a MongoDB document.
     *
     * @param document MongoDB document.
     * @return Warehouse article.
     */
    public static WarehouseArticle fromDocument(final Document document) {
        return new WarehouseArticle(
                document.getLong("branchId"),
                new Article(
                        document.getLong("articleId"),
                        document.getString("name"),
                        new BigDecimal(document.getString("price")),
                        document.getInteger("minStock"),
                        document.getInteger("stock")
                )
        );
    }

    /**
     * Creates a MongoDB document from a warehouse article.
     *
     * @param warehouseArticle Warehouse article.
     * @return MongoDB document.
     */
    public static Document toDocument(final WarehouseArticle warehouseArticle) {
        return new Document()
                .append("branchId", warehouseArticle.branchId)
                .append("articleId", warehouseArticle.article.articleId())
                .append("name", warehouseArticle.article.name())
                .append("price", warehouseArticle.article.price().toPlainString())
                .append("minStock", warehouseArticle.article.minStock())
                .append("stock", warehouseArticle.article.stock());
    }
}
