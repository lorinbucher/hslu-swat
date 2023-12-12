package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements order delivery processing.
 */
public final class DeliveryProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryProcessor.class);

    private final MessagePublisher<LogEventDTO> eventLogger;

    private final ProductCatalog catalog;
    private final Deliveries deliveries;

    /**
     * Constructor.
     *
     * @param messagePublisher Log message publisher.
     * @param deliveries       Product catalog warehouse.
     * @param catalog          Deliverys warehouse.
     */
    public DeliveryProcessor(final MessagePublisher<LogEventDTO> messagePublisher,
                             final ProductCatalog catalog, final Deliveries deliveries) {
        this.eventLogger = messagePublisher;
        this.catalog = catalog;
        this.deliveries = deliveries;
    }

    /**
     * Processed deliveries.
     */
    @Override
    public void run() {
        LOG.info("Starting scheduled delivery processing");
        processDelivered();
        processNewAndModified();
        processWaitingAndReady();
        LOG.info("Finished scheduled delivery processing");
    }

    /**
     * Processes delivered deliveries.
     */
    private void processDelivered() {
        LOG.info("Start processing delivered deliveries");
        for (WarehouseEntity<Delivery> entity : deliveries.getAllByStatus(DeliveryStatus.DELIVERED)) {
            Delivery delivery = (Delivery) entity.entity();
            LOG.info("Processing delivered delivery {} from branch {}", delivery.orderNumber(), entity.branchId());

            List<DeliveryArticle> articles = processArticlesDelivered(entity.branchId(), delivery.articles());
            boolean completed = articles.stream().allMatch(a -> a.status() == DeliveryArticleStatus.DELIVERED);
            DeliveryStatus updatedStatus = completed ? DeliveryStatus.COMPLETED : DeliveryStatus.DELIVERED;
            Delivery updated = new Delivery(delivery.orderNumber(), updatedStatus, articles);
            deliveries.update(entity.branchId(), delivery.orderNumber(), updated);

            if (completed) {
                String message = "All articles for order " + delivery.orderNumber() + " are delivered";
                LogEventDTO event = new LogEventDTO(entity.branchId(), "delivery.delivered", message);
                eventLogger.sendMessage(Routes.LOG_EVENT, event);
                LOG.info("Completed delivery {} from branch {}", delivery.orderNumber(), entity.branchId());
            } else {
                LOG.warn("Delivery {} from branch {} cannot be delivered", delivery.orderNumber(), entity.branchId());
            }
        }
        LOG.info("Finished processing delivered deliveries");
    }

    /**
     * Processes new and modified deliveries.
     */
    private void processNewAndModified() {

    }

    /**
     * Processes waiting and ready deliveries.
     */
    private void processWaitingAndReady() {

    }

    /**
     * Processes the delivered articles.
     *
     * @param branchId ID of the branch.
     * @param articles Delivery articles to process.
     * @return Processed delivery articles.
     */
    private List<DeliveryArticle> processArticlesDelivered(final long branchId, final List<DeliveryArticle> articles) {
        List<DeliveryArticle> delivered = new ArrayList<>();
        for (DeliveryArticle a : articles) {
            DeliveryArticle updated = a;
            if (a.status() != DeliveryArticleStatus.DELIVERED) {
                boolean inStock = catalog.changeStock(branchId, a.articleId(), -a.quantity());
                if (inStock) {
                    catalog.changeReserved(branchId, a.articleId(), -a.quantity());
                    updated = new DeliveryArticle(a.articleId(), a.quantity(), DeliveryArticleStatus.DELIVERED);
                } else {
                    LOG.warn("Not enough items of article {} from branch {} in stock", a.articleId(), branchId);
                }
            }
            delivered.add(updated);
        }
        return delivered;
    }

    /**
     * Processes the articles.
     *
     * @param branchId ID of the branch.
     * @param articles Delivery articles to process.
     * @return Processed delivery articles.
     */
    private List<DeliveryArticle> processArticles(final long branchId, final List<DeliveryArticle> articles) {
        return List.of();
    }
}
