package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Receives messages from Rabbit MQ.
 */
@Singleton
public final class MessageListenerRMQ implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageListenerRMQ.class);

    private final RabbitMqConfig config;
    private final BusConnector bus;

    /**
     * Constructor.
     */
    public MessageListenerRMQ() {
        this.config = new RabbitMqConfig();
        this.bus = new BusConnector(config);
        this.bus.connectWithRetry();
    }

    @Override
    public void receiveMessages(final String route, final Consumer<String> callback) {
        try {
            LOG.info("Start listening for messages with routing '{}'", route);
            bus.listenFor(config.getExchange(), "WarehouseService <- " + route, route,
                    (String routeReceived, String replyTo, String corrId, String message) -> {
                        LOG.info("Received message with routing '{}': {}", routeReceived, message);
                        callback.accept(message);
                    });
        } catch (IOException e) {
            LOG.error("Failed to receive message with routing '{}': {}", route, e.getMessage());
        }
    }
}
