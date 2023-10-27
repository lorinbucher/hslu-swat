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
package ch.hslu.swda.bus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Liest Konfiguration für RabbitMQ aus Propertyfile.
 */
public final class RabbitMqConfig {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMqConfig.class);
    private static final String EXCHANGE = "exchange";
    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private static final String HOST_PROPERTY = "host";
    private static final String HOST_ENV = "RMQ_HOST";
    private static final String CONFIG_FILE_NAME = "rabbitmq.properties";

    private final Properties properties = new Properties();

    /**
     * Liest die Konfiguration vom Default-File ein.
     */
    public RabbitMqConfig() {
        this(CONFIG_FILE_NAME);
    }

    /**
     * Liest die Konfiguration ein.
     *
     * @param fileName Dateiname.
     */
    RabbitMqConfig(final String fileName) {
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(inputStream);
            assert inputStream != null;
            inputStream.close();
        } catch (IOException e) {
            LOG.error("Error while reading from file {}", CONFIG_FILE_NAME);
        }
    }

    /**
     * @return Liefert das Attribut host.
     */
    String getHost() {
        String host = System.getenv(HOST_ENV);
        if (host != null) {
            return host;
        }
        return this.properties.getProperty(HOST_PROPERTY);
    }

    /**
     * @return Liefert das Attribut user.
     */
    String getUsername() {
        return this.properties.getProperty(USER);
    }

    /**
     * @return Liefert das Attribut password.
     */
    String getPassword() {
        return this.properties.getProperty(PASSWORD);
    }

    /**
     * @return Liefert das Attribut exchange.
     */
    public String getExchange() {
        return this.properties.getProperty(EXCHANGE);
    }
}
