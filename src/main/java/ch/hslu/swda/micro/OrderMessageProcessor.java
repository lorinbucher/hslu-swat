package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.dto.OrderDTO;
import ch.hslu.swda.entities.Delivery;
import ch.hslu.swda.entities.DeliveryArticle;
import ch.hslu.swda.entities.DeliveryArticleStatus;
import ch.hslu.swda.entities.DeliveryStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

/**
 * Implements the order message processing.
 */
public final class OrderMessageProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(OrderMessageProcessor.class);

    private final MessageListener messageListener;

    private final Deliveries deliveries;

    /**
     * Constructor.
     *
     * @param listener   Message listener.
     * @param deliveries Deliveries warehouse.
     */
    public OrderMessageProcessor(final MessageListener listener, final Deliveries deliveries) {
        this.messageListener = listener;
        this.deliveries = deliveries;
    }

    /**
     * Listens for incoming messages and processes them.
     */
    @Override
    public void run() {
        messageListener.receiveMessages(Routes.NEW_ORDER, this::process);
    }

    /**
     * Registers the delivery for the order message.
     *
     * @param message Order message.
     */
    public void process(final String message) {
        OrderDTO order = parseMessage(message);
        if (order != null) {
            List<DeliveryArticle> deliveryArticles = order.articles().stream()
                    .map(a -> new DeliveryArticle(a.articleId(), a.quantity(), DeliveryArticleStatus.PROCESSING))
                    .toList();
            Delivery exists = deliveries.getById(order.branchId(), order.orderNumber());
            if (deliveryArticles.isEmpty() && exists == null) {
                LOG.info("Not registering new delivery {} for branch {}", order.orderNumber(), order.branchId());
            } else if (exists == null) {
                LOG.info("Registering new delivery {} for branch {}", order.orderNumber(), order.branchId());
                Delivery delivery = new Delivery(order.orderNumber(), DeliveryStatus.NEW, deliveryArticles);
                deliveries.create(order.branchId(), delivery);
            } else {
                LOG.info("Updating delivery {} for branch {}", order.orderNumber(), order.branchId());
                updateDelivery(order.branchId(), exists, deliveryArticles);
            }
        } else {
            LOG.error("Not registering new delivery, parsing order failed");
        }
    }

    /**
     * Updates an existing delivery based on the current state of the delivery.
     *
     * @param branchId ID of the branch.
     * @param existing Existing delivery.
     * @param articles Updated list of delivery articles.
     */
    private void updateDelivery(final long branchId, final Delivery existing, final List<DeliveryArticle> articles) {
        Delivery delivery = null;
        List<DeliveryArticle> deliveryArticles = articles;
        switch (existing.status()) {
            case NEW:
                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.NEW, deliveryArticles);
                break;
            case MODIFIED:
                deliveryArticles = Stream.concat(
                        existing.articles().stream().filter(a -> a.status() != DeliveryArticleStatus.PROCESSING),
                        articles.stream()).toList();
                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.MODIFIED, deliveryArticles);
                break;
            case COMPLETED:
                LOG.error("Delivery {} for branch {} already completed", existing.orderNumber(), branchId);
                break;
            default:
                deliveryArticles = Stream.concat(existing.articles().stream(), articles.stream()).toList();
                delivery = new Delivery(existing.orderNumber(), DeliveryStatus.MODIFIED, deliveryArticles);
                break;
        }

        if (delivery != null) {
            deliveries.update(branchId, existing.orderNumber(), delivery);
        }
    }

    /**
     * Parses the order message.
     *
     * @param message Order message.
     * @return Order.
     */
    private OrderDTO parseMessage(final String message) {
        OrderDTO dto = null;
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            dto = mapper.readValue(message, OrderDTO.class);
            LOG.info("Parsed order message: {}", dto);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to parse order message: {}", e.getMessage());
        }
        return dto;
    }
}
