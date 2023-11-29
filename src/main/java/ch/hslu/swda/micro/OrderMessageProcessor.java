package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.dto.ArticleGetDTO;
import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryArticle;
import ch.hslu.swda.entities.DeliveryArticleStatus;
import ch.hslu.swda.entities.DeliveryStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

/**
 * Implements the order message processing.
 */
public class OrderMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(OrderMessageProcessor.class);

    private final Deliveries deliveries;

    /**
     * Constructor.
     */
    public OrderMessageProcessor(final Deliveries deliveries) {
        this.deliveries = deliveries;
    }

//    /**
//     * Registers the delivery for the order for further processing.
//     *
//     * @param request Order details.
//     * @return True if the delivery was processed successfully, false if not.
//     */
//    public boolean process(final String message) {
//        List<DeliveryArticle> deliveryArticles = request.articles().stream()
//                .map(a -> new DeliveryArticle(a.articleId(), a.quantity(), DeliveryArticleStatus.PROCESSING))
//                .toList();
//
//        boolean result;
//        Delivery exists = deliveries.getById(request.branchId(), request.orderNumber());
//        if (deliveryArticles.isEmpty() && exists == null) {
//            LOG.info("Not registering new delivery {} for branch {}", request.orderNumber(), request.branchId());
//            result = false;
//        } else if (exists == null) {
//            LOG.info("Registering new delivery {} for branch {}", request.orderNumber(), request.branchId());
//            Delivery delivery = new Delivery(request.orderNumber(), DeliveryStatus.NEW, deliveryArticles);
//            result = deliveries.create(request.branchId(), delivery) != null;
//        } else {
//            LOG.info("Updating delivery {} for branch {}", request.orderNumber(), request.branchId());
//            result = updateDelivery(request.branchId(), exists, deliveryArticles);
//        }
//        return result;
//    }
//
//    /**
//     * Updates an existing delivery based on the current state of the delivery.
//     *
//     * @param branchId ID of the branch.
//     * @param existing Existing delivery.
//     * @param articles Updated list of delivery articles.
//     * @return True if the delivery was updated successfully.
//     */
//    private boolean updateDelivery(final long branchId, final Delivery existing, final List<DeliveryArticle> articles) {
//        Delivery delivery = null;
//        List<DeliveryArticle> deliveryArticles = articles;
//        switch (existing.status()) {
//            case NEW:
//                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.NEW, deliveryArticles);
//                break;
//            case CHANGED:
//                deliveryArticles = Stream.concat(
//                        existing.articles().stream().filter(a -> a.status() != DeliveryArticleStatus.PROCESSING),
//                        articles.stream()).toList();
//                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.CHANGED, deliveryArticles);
//                break;
//            case COMPLETED:
//                LOG.error("Delivery {} for branch {} already completed", existing.orderNumber(), branchId);
//                break;
//            default:
//                deliveryArticles = Stream.concat(existing.articles().stream(), articles.stream()).toList();
//                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.CHANGED, deliveryArticles);
//                break;
//        }
//
//        boolean result = false;
//        if (delivery != null) {
//            result = deliveries.update(branchId, existing.orderNumber(), delivery) != null;
//        }
//        return result;
//    }
}
