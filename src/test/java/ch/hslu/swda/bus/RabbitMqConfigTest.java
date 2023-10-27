/*
 * Copyright 2023 Roland Gisler, Hochschule Luzern - Informatik.
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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test method for {@link ch.hslu.swda.bus.RabbitMqConfig}.
 */
final class RabbitMqConfigTest {

    /**
     * Test method for {@link ch.hslu.swda.bus.RabbitMqConfig#getHost()}.
     */
    @Test
    void testGetHost() {
        assertThat(new RabbitMqConfig("rabbitmq.test.properties").getHost()).isEqualTo("1111");
    }

    /**
     * Test method for {@link ch.hslu.swda.bus.RabbitMqConfig#getUsername()}.
     */
    @Test
    void testGetUsername() {
        assertThat(new RabbitMqConfig("rabbitmq.test.properties").getUsername()).isEqualTo("2222");
    }

    /**
     * Test method for {@link ch.hslu.swda.bus.RabbitMqConfig#getPassword()}.
     */
    @Test
    void testGetPassword() {
        assertThat(new RabbitMqConfig("rabbitmq.test.properties").getPassword()).isEqualTo("3333");
    }

    /**
     * Test method for {@link ch.hslu.swda.bus.RabbitMqConfig#getExchange()}.
     */
    @Test
    void testGetExchange() {
        assertThat(new RabbitMqConfig("rabbitmq.test.properties").getExchange()).isEqualTo("4444");
    }

}
