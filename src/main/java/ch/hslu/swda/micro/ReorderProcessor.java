package ch.hslu.swda.micro;

import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.Reorders;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.Article;
import ch.hslu.swda.entities.WarehouseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implements reorder processing.
 */
public final class ReorderProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ReorderProcessor.class);

    private final MessagePublisher<LogEventDTO> messagePublisher;

    private final ProductCatalog productCatalog;
    private final Reorders reorders;

    /**
     * Constructor.
     *
     * @param messagePublisher Log message publisher.
     * @param productCatalog   Product catalog warehouse.
     * @param reorders         Reorders warehouse.
     */
    public ReorderProcessor(final MessagePublisher<LogEventDTO> messagePublisher,
                            final ProductCatalog productCatalog, final Reorders reorders) {
        this.messagePublisher = messagePublisher;
        this.productCatalog = productCatalog;
        this.reorders = reorders;
    }

    /**
     * Reorders articles that fall below minimum stock and processes reorders.
     */
    @Override
    public void run() {
        LOG.info("Starting scheduled reorder processing");
        reorderArticlesWithLowStock();
        LOG.info("Finished scheduled reorder processing");
    }

    /**
     * Reorders articles that fall below the minimum stock.
     */
    private void reorderArticlesWithLowStock() {
        LOG.info("Reordering articles with low stock");
        List<WarehouseEntity<Article>> entities = productCatalog.getLowStock();
        for (WarehouseEntity<Article> entity : entities) {
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
}
