package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.DeliveriesDB;
import ch.hslu.swda.dto.ArticleDeliveredDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implements the article delivered message handler.
 */
@Singleton
public class OrderMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OrderMessageHandler.class);

    private final Deliveries deliveries;

    private final RabbitMqConfig config;
    private final BusConnector bus;

    /**
     * Constructor.
     */
    public OrderMessageHandler() {
        this.deliveries = new DeliveriesDB();
        this.config = new RabbitMqConfig();
        this.bus = new BusConnector(config);
        this.connectToBus();
    }

    /**
     * Connects to Rabbit MQ and listens for incoming messages.
     */
    private void connectToBus() {
        boolean connected = false;
        while (!connected) {
            try {
                LOG.info("Try connecting to message bus...");
                bus.connect();
                LOG.info("Starting listening for messages with routing [{}]", Routes.ORDER_CONFIRMED);
                bus.listenFor(config.getExchange(), "WarehouseService <- " + Routes.ORDER_CONFIRMED,
                        Routes.ORDER_CONFIRMED, (String route, String replyTo, String corrId, String message) ->
                                new OrderMessageProcessor(deliveries).process(message));
                connected = true;
            } catch (IOException | TimeoutException e) {
                LOG.error(e.getMessage(), e);
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException ie) {
                    LOG.warn("Reconnection timeout interrupted: {}", ie.getMessage());
                }
            }
        }
    }

    /**
     * Publishes a article delivered message.
     *
     * @param delivered Log event.
     */
    public void publishDelivered(final ArticleDeliveredDTO delivered) {
        try {
            String message = new ObjectMapper().writeValueAsString(delivered);
            LOG.info("Sending article delivered message: {}", message);
            this.bus.talkAsync(config.getExchange(), Routes.ARTICLE_DELIVERED, message);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize article delivered message: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("Failed to send article delivered message: {}", e.getMessage());
        }
    }
}
