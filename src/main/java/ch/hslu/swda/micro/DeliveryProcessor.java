package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryArticle;
import ch.hslu.swda.entities.DeliveryArticleStatus;
import ch.hslu.swda.entities.DeliveryStatus;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements order delivery processing.
 */
@Singleton
public class DeliveryProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryProcessor.class);

    private final Deliveries deliveries;
    private final ProductCatalog productCatalog;

    /**
     * Constructor.
     */
    public DeliveryProcessor(final Deliveries deliveries, final ProductCatalog productCatalog) {
        this.deliveries = deliveries;
        this.productCatalog = productCatalog;
    }

    /**
     * Changes the status of the delivery to the completed status.
     *
     * @param branchId    ID of the branch.
     * @param orderNumber Order number.
     * @return Delivery.
     */
    public Delivery changeToCompleted(final long branchId, final long orderNumber) {
        Delivery delivery = deliveries.getById(branchId, orderNumber);
        if (delivery != null) {
            // TODO (lorin): Temporary disabled until delivery processing is working
            //if (delivery.status() != DeliveryStatus.READY) {
            //    LOG.warn("REST: Delivery {} from branch {} is not ready yet", orderNumber, branchId);
            //    throw new IllegalStateException("Delivery is not ready yet");
            //}
            Delivery completedDelivery = new Delivery(orderNumber, DeliveryStatus.COMPLETED, delivery.articles()
                    .stream()
                    .map(a -> new DeliveryArticle(a.articleId(), a.quantity(), DeliveryArticleStatus.DELIVERED))
                    .toList());
            delivery = deliveries.update(branchId, orderNumber, completedDelivery);
        }
        return delivery;
    }
}
