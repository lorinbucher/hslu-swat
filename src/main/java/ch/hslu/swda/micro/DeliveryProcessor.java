package ch.hslu.swda.micro;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.dto.LogEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements order delivery processing.
 */
public final class DeliveryProcessor implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryProcessor.class);

    private final MessagePublisher<LogEventDTO> eventLogger;

    private final ProductCatalog catalog;
    private final Deliveries deliveries;

    /**
     * Constructor.
     *
     * @param messagePublisher Log message publisher.
     * @param deliveries       Product catalog warehouse.
     * @param catalog          Reorders warehouse.
     */
    public DeliveryProcessor(final MessagePublisher<LogEventDTO> messagePublisher,
                             final ProductCatalog catalog, final Deliveries deliveries) {
        this.eventLogger = messagePublisher;
        this.catalog = catalog;
        this.deliveries = deliveries;
    }

    /**
     * Processed deliveries.
     */
    @Override
    public void run() {
        LOG.info("Starting scheduled delivery processing");
        LOG.info("Finished scheduled delivery processing");
    }
}
