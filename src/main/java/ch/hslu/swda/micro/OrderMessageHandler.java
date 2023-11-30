package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.DeliveriesDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implements the order message handler.
 */
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

        String threadName = Thread.currentThread().getName();
        LOG.info("[Thread: {}] Service started", threadName);

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
                LOG.info("Starting listening for messages with routing [{}]", Routes.NEW_ORDER);
                bus.listenFor(config.getExchange(), "OrderService <- " + Routes.NEW_ORDER,
                        Routes.NEW_ORDER, (String route, String replyTo, String corrId, String message) ->
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
}
