package ch.hslu.swda.micronaut;

import ch.hslu.swda.business.Deliveries;
import ch.hslu.swda.business.DeliveriesDB;
import ch.hslu.swda.business.ProductCatalog;
import ch.hslu.swda.business.ProductCatalogDB;
import ch.hslu.swda.dto.OrderDTO;
import ch.hslu.swda.micro.*;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main Application.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "SWDA Warehouse G08",
                version = "1.0",
                license = @License(identifier = "Apache-2.0", name = "Apache License Version 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0")),
        servers = {
                @Server(url = "https://warehouse.g08.swda.hslu-edu.ch"),
                @Server(url = "http://localhost:8088")
        }
)
public final class Application {

    /**
     * Private Constructor.
     */
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

        MessageListener messageListener = new MessageListenerRMQ();
        MessagePublisher<OrderDTO> articleMessagePublisher = new MessagePublisherRMQ<>();

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new ArticleMessageProcessor(messageListener, articleMessagePublisher, productCatalog));
        executorService.submit(new OrderMessageProcessor(messageListener, deliveries));
    }
}
