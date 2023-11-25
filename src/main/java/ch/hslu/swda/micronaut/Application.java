package ch.hslu.swda.micronaut;

import ch.hslu.swda.micro.ArticleMessageHandler;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

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

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

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
        new Thread(ArticleMessageHandler::new, "ArticleMessageHandler").start();
    }
}
