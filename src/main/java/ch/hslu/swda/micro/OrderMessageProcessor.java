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

import java.util.ArrayList;
import java.util.List;

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
        messageListener.receiveMessages(Routes.ORDER, this::process);
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
                    .map(a -> new DeliveryArticle(a.articleId(), a.quantity(),
                            a.action() != null ? a.action() : DeliveryArticleStatus.ADD))
                    .toList();

            Delivery exists = deliveries.getById(order.branchId(), order.orderNumber());
            if (deliveryArticles.isEmpty() && exists == null) {
                LOG.info("Not registering empty delivery {} for branch {}", order.orderNumber(), order.branchId());
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
        if (existing.status() != DeliveryStatus.DELIVERED && existing.status() != DeliveryStatus.COMPLETED) {
            List<DeliveryArticle> deliveryArticles = new ArrayList<>(existing.articles());
            if (articles.isEmpty()) {
                deliveryArticles.addAll(existing.articles().stream()
                        .map(a -> new DeliveryArticle(a.articleId(), a.quantity(), DeliveryArticleStatus.REMOVE))
                        .toList());
            } else {
                deliveryArticles.addAll(articles);
            }
            Delivery delivery = new Delivery(existing.orderNumber(), DeliveryStatus.MODIFIED, deliveryArticles);
            deliveries.update(branchId, existing.orderNumber(), delivery);
        } else {
            LOG.error("Delivery {} for branch {} already delivered", existing.orderNumber(), branchId);
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
