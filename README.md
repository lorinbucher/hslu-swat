# Module SWDA - Warehouse Microservice

![CI](https://github.com/lorinbucher/hslu-swat/actions/workflows/ci.yml/badge.svg)

The Warehouse microservice, a component of the SWDA module, manages articles, reorders, and deliveries within the order
system.

## Build

The local build requires a working Docker environment!

- `mvn package` - creates a shade JAR (service.jar) and a Docker image.
- `mvn verify` - runs integration tests (with TestContainer).

## Run

Provided the backbone is running, the service can be started locally in various ways:

### IDE

- In the IDE, start the `main` method of the class `ch.hslu.swda.micronaut.Application`.

### Console

- Java: `java -jar target/service.jar`
- Maven: `mvn exec:java`
- Maven with Docker (interactive): `mvn docker:run`
- Maven with Docker (daemon):
    - `mvn docker:start` - Start the container
    - `mvn docker:logs` - Show the logs
    - `mvn docker:stop` - Stop and remove the container
- Docker: `docker run --rm -it -e "RMQ_HOST=host.docker.internal" swda-23hs01/warehouse`

## Documentation

The documentation using AsciiDoc can be found in [`src/docs/asciidoc`](src/docs/asciidoc/index.adoc).
