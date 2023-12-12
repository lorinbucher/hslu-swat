package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.Reorders;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.Article;
import ch.hslu.swda.entities.Reorder;
import ch.hslu.swda.entities.ReorderStatus;
import ch.hslu.swda.entities.WarehouseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implements reorder processing.
 */
public final class ReorderProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ReorderProcessor.class);

    private final MessagePublisher<LogEventDTO> eventLogger;

    private final ProductCatalog catalog;
    private final Reorders reorders;

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
        for (WarehouseEntity<Reorder> reorderEntity : reorders.getAllByStatus(ReorderStatus.DELIVERED)) {
            Reorder reorder = (Reorder) reorderEntity.entity();
            LOG.info("Processing delivered reorder {} from branch {}", reorder.reorderId(), reorderEntity.branchId());

            boolean result = catalog.changeStock(reorderEntity.branchId(), reorder.articleId(), reorder.quantity());
            if (result) {
                reorders.updateStatus(reorderEntity.branchId(), reorder.reorderId(), ReorderStatus.COMPLETED);
                String message = "Received delivery for reorder " + reorder.reorderId() + " from central warehouse";
                LogEventDTO event = new LogEventDTO(reorderEntity.branchId(), "reorder.delivered", message);
                eventLogger.sendMessage(Routes.LOG_EVENT, event);
                LOG.info("Reorder {} from branch {} completed", reorder.reorderId(), reorderEntity.branchId());
            } else {
                LOG.error("Reorder {} from branch {} not completed", reorder.reorderId(), reorderEntity.branchId());
            }
        }
        LOG.info("Finished processing delivered reorders");
    }

    /**
     * Processes the new reorders.
     */
    private void processNewReorders() {
        LOG.info("Start processing new reorders");
        LOG.info("Finished processing new reorders");
    }
}
