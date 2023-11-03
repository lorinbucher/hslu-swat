/*
 * Copyright 2023 Roland Christen, HSLU Informatik, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hslu.swda.micro;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

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
    }
}
