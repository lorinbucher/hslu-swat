package ch.hslu.swda.micronaut;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.DeliveriesDB;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogDB;
import ch.hslu.swda.business.Reorders;
import ch.hslu.swda.business.ReordersDB;
import ch.hslu.swda.dto.LogEventDTO;
import ch.hslu.swda.dto.OrderDTO;
import ch.hslu.swda.micro.ArticleMessageProcessor;
import ch.hslu.swda.micro.DeliveryProcessor;
import ch.hslu.swda.micro.MessageListener;
import ch.hslu.swda.micro.MessageListenerRMQ;
import ch.hslu.swda.micro.MessagePublisher;
import ch.hslu.swda.micro.MessagePublisherRMQ;
import ch.hslu.swda.micro.OrderMessageProcessor;
import ch.hslu.swda.micro.ReorderProcessor;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main Application.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "SWDA Warehouse G08",
                version = "1.0",
                license = @License(
                        identifier = "Apache-2.0",
                        name = "Apache License Version 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "https://warehouse.g08.swda.hslu-edu.ch"),
                @Server(url = "http://localhost:8088")
        }
)
public final class Application {

    private Application() {
    }

    /**
     * Starts the warehouse microservice.
     *
     * @param args not used.
     */
    public static void main(final String[] args) {
        Micronaut.run(Application.class);

        Deliveries deliveries = new DeliveriesDB();
        ProductCatalog productCatalog = new ProductCatalogDB();
        Reorders reorders = new ReordersDB();

        MessageListener messageListener = new MessageListenerRMQ();
        MessagePublisher<LogEventDTO> logEventMessagePublisher = new MessagePublisherRMQ<>();
        MessagePublisher<OrderDTO> articleMessagePublisher = new MessagePublisherRMQ<>();

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new ArticleMessageProcessor(messageListener, articleMessagePublisher, productCatalog));
        executor.submit(new OrderMessageProcessor(messageListener, deliveries));

        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
        scheduledExecutor.scheduleAtFixedRate(
                new DeliveryProcessor(logEventMessagePublisher, productCatalog, deliveries),
                15, 30, TimeUnit.SECONDS);
        scheduledExecutor.scheduleAtFixedRate(
                new ReorderProcessor(logEventMessagePublisher, productCatalog, reorders),
                30, 30, TimeUnit.SECONDS);
    }
}
