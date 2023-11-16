package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Implements the article request message handler.
 */
public final class ArticleMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleMessageHandler.class);

    private final RabbitMqConfig config;
    private final BusConnector bus;

    /**
     * Constructor.
     */
    public ArticleMessageHandler() throws IOException, TimeoutException {
        this.config = new RabbitMqConfig();
        this.bus = new BusConnector(config);

        String threadName = Thread.currentThread().getName();
        LOG.info("[Thread: {}] Service started", threadName);

        bus.connect();
        LOG.info("Starting listening for messages with routing [{}]", Routes.ARTICLE_GET);
        bus.listenFor(config.getExchange(), "WarehouseService <- " + Routes.ARTICLE_GET, Routes.ARTICLE_GET,
                (String route, String replyTo, String corrId, String message) -> receiveMessages(route, message));
    }

    /**
     * Handles incoming messages for article requests.
     *
     * @param route   Route of the message.
     * @param message Message.
     */
    private void receiveMessages(final String route, final String message) {
        final String threadName = Thread.currentThread().getName();
        LOG.info("[Thread: {}] Begin message processing", threadName);
        LOG.info("Received message with routing [{}] {}", route, message);

        try {
            ArticleMessageProcessor messageProcessor = new ArticleMessageProcessor();
            String response = messageProcessor.process(message);
            bus.talkAsync(config.getExchange(), Routes.ARTICLE_RETURN, response);
        } catch (IllegalArgumentException e) {
            LOG.error("Parsing message failed, not sending a response");
        } catch (IOException e) {
            LOG.error("Sending message to route {} failed: {}", Routes.ARTICLE_RETURN, e.getMessage());
        } finally {
            LOG.info("[Thread: {}] End message processing", threadName);
        }
    }
}
