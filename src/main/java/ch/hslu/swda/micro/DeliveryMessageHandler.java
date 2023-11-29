package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.dto.ArticleDeliveredDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Publishes order delivered messages to the article delivered message queue.
 */
@Singleton
public class DeliveryMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryMessageHandler.class);

    private final RabbitMqConfig config;
    private final BusConnector bus;

    /**
     * Constructor.
     */
    public DeliveryMessageHandler() {
        this.config = new RabbitMqConfig();
        this.bus = new BusConnector(config);
        this.bus.connectWithRetry();
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
