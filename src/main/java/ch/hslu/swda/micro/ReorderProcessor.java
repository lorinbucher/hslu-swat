package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.Reorders;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.Article;
import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
import ch.hslu.swda.entities.WarehouseEntity;
import ch.hslu.swda.stock.api.Stock;
import ch.hslu.swda.stock.local.StockLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements reorder processing.
 */
public final class ReorderProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ReorderProcessor.class);

    private final MessagePublisher<LogEventDTO> eventLogger;

    private final ProductCatalog catalog;
    private final Reorders reorders;

    private final Stock stock;

    /**
     * Constructor.
     *
     * @param messagePublisher Log message publisher.
     * @param catalog          Product catalog warehouse.
     * @param reorders         Reorders warehouse.
     */
    public ReorderProcessor(final MessagePublisher<LogEventDTO> messagePublisher,
                            final ProductCatalog catalog, final Reorders reorders) {
        this.eventLogger = messagePublisher;
        this.catalog = catalog;
        this.reorders = reorders;
        this.stock = new StockLocal();
    }

    /**
     * Reorders articles that fall below minimum stock and processes reorders.
     */
    @Override
    public void run() {
        LOG.info("Starting scheduled reorder processing");
        processDeliveredReorders();
        reorderArticlesWithLowStock();
        processNewReorders();
        LOG.info("Finished scheduled reorder processing");
    }

    /**
     * Reorders articles that fall below the minimum stock.
     */
    private void reorderArticlesWithLowStock() {
        LOG.info("Start reordering articles with low stock");
        for (WarehouseEntity<Article> entity : catalog.getLowStock()) {
            Article article = (Article) entity.entity();
            int reordered = reorders.countReorderedArticles(entity.branchId(), article.articleId());
            int predictedStock = article.stock() - article.reserved() + reordered;
            if (predictedStock < article.minStock()) {
                int quantity = article.minStock() * 2 - predictedStock;
                LOG.info("Registering reorder of {} articles with id {} for branch {}",
                        quantity, article.articleId(), entity.branchId());
                reorders.create(entity.branchId(), article.articleId(), quantity);
            }
        }
        LOG.info("Finished reordering articles with low stock");
    }

    /**
     * Processes the delivered reorders.
     */
    private void processDeliveredReorders() {
        LOG.info("Start processing delivered reorders");
        for (WarehouseEntity<Reorder> entity : reorders.getAllByStatus(ReorderStatus.DELIVERED)) {
            Reorder reorder = (Reorder) entity.entity();
            LOG.info("Processing delivered reorder {} from branch {}", reorder.reorderId(), entity.branchId());

            catalog.changeStock(entity.branchId(), reorder.articleId(), reorder.quantity());
            reorders.updateStatus(entity.branchId(), reorder.reorderId(), ReorderStatus.COMPLETED);
            LOG.info("Completed reorder {} from branch {}", reorder.reorderId(), entity.branchId());

            String message = "Received delivery for reorder " + reorder.reorderId() + " from central warehouse";
            LogEventDTO event = new LogEventDTO(entity.branchId(), "reorder.delivered", message);
            eventLogger.sendMessage(Routes.LOG_EVENT, event);
        }
        LOG.info("Finished processing delivered reorders");
    }

    /**
     * Processes the new reorders.
     */
    private void processNewReorders() {
        LOG.info("Start processing new reorders");
        for (WarehouseEntity<Reorder> entity : reorders.getAllByStatus(ReorderStatus.NEW)) {
            Reorder reorder = (Reorder) entity.entity();
            LOG.info("Processing new reorder {} from branch {}", reorder.reorderId(), entity.branchId());

            int ordered = orderArticles((int) reorder.articleId(), reorder.quantity());
            if (ordered > 0) {
                reorders.updateQuantity(entity.branchId(), reorder.reorderId(), ordered);
                reorders.updateStatus(entity.branchId(), reorder.reorderId(), ReorderStatus.WAITING);
                LOG.info("Reordered {} items of article {} for branch {}",
                        ordered, reorder.articleId(), entity.branchId());

                String message = "Ordered " + ordered + " items of " + reorder.articleId() + " from central warehouse";
                LogEventDTO event = new LogEventDTO(entity.branchId(), "reorder.new", message);
                eventLogger.sendMessage(Routes.LOG_EVENT, event);
            } else {
                LOG.error("Failed to reorder article {} for branch {}", reorder.articleId(), entity.branchId());
            }
        }
        LOG.info("Finished processing new reorders");
    }

    /**
     * Reorders an article from the central warehouse.
     *
     * @param articleId ID of the article.
     * @param quantity  Number of items to order.
     * @return Number of ordered articles.
     */
    private int orderArticles(final int articleId, final int quantity) {
        int stockCount = stock.getItemCount(articleId);
        int ordered;
        if (stockCount >= quantity) {
            ordered = stock.orderItem(articleId, quantity);
        } else {
            LOG.info("Not anough articles in central warehouse stock, ordering only {} items", stockCount);
            ordered = stock.orderItem(articleId, stockCount);
        }
        return ordered;
    }
}
