package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Publishes messages to Rabbit MQ.
 */
@Singleton
public final class MessagePublisherRMQ<T> implements MessagePublisher<T> {

    private static final Logger LOG = LoggerFactory.getLogger(MessagePublisherRMQ.class);

    private final RabbitMqConfig config;
    private final BusConnector bus;

    /**
     * Constructor.
     */
    public MessagePublisherRMQ() {
        this.config = new RabbitMqConfig();
        this.bus = new BusConnector(config);
        this.bus.connectWithRetry();
    }

    @Override
    public String sendMessage(final String route, final T messageObject) {
        String message = "";
        try {
            message = new ObjectMapper().writeValueAsString(messageObject);
            LOG.info("Sending message to route '{}': {}", route, message);
            this.bus.talkAsync(config.getExchange(), route, message);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize message: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("Failed to send message: {}", e.getMessage());
        }
        return message;
    }
}
