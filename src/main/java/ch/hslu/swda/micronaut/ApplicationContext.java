package ch.hslu.swda.micronaut;

import io.micronaut.context.event.ShutdownEvent;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application context for micronaut.
 */
@Singleton
public final class ApplicationContext {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);

    /**
     * Runs on startup event received from micronaut.
     *
     * @param event Startup event.
     */
    @EventListener
    public void onStartup(final StartupEvent event) {
        LOG.info("Micronaut starting - establishing connection...");
    }

    /**
     * Runs on shutdown event received from micronaut.
     *
     * @param event Shutdown event.
     */
    @EventListener
    public void onShutdown(final ShutdownEvent event) {
        LOG.info("Micronaut stopping - connection terminating...");
    }
}
