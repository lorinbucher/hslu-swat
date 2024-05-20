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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement the connection to the RabbitMQ message broker.
 */
public final class BusConnector implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(BusConnector.class);
    private final RabbitMqConfig config;

    private Connection connection;

    private Channel channelTalk;
    private Channel channelListen;

    public BusConnector(final RabbitMqConfig config) {
        this.config = config;
    }

    /**
     * Sends a message to RabbitMQ asynchronously.
     *
     * @param exchange RabbitMQ exchange.
     * @param route    Message route.
     * @param message  Message.
     * @throws IOException Exception if sending message failed.
     */
    public void talkAsync(final String exchange, final String route, final String message) throws IOException {
        AMQP.BasicProperties props = new AMQP.BasicProperties();
        channelTalk.basicPublish(exchange, route, props, message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Listens for incoming messages asynchronously.
     *
     * @param exchange  RabbitMQ exchange.
     * @param queueName Message queue.
     * @param route     Message route.
     * @param receiver  Message receiver.
     * @throws IOException Exception if reading message failed.
     */
    public void listenFor(final String exchange, final String queueName, final String route,
                          final MessageReceiver receiver) throws IOException {

        channelListen.queueDeclare(queueName, true, false, true, new HashMap<>());
        channelListen.queueBind(queueName, exchange, route);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            receiver.onMessageReceived(route, delivery.getProperties().getReplyTo(), delivery.getProperties().getCorrelationId(), message);
        };
        channelListen.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }


    /**
     * Connects to Rabbit MQ retrying in case of a failure.
     */
    public void connectWithRetry() {
        boolean connected = false;
        while (!connected) {
            try {
                LOG.info("Try connecting to message bus...");
                connect();
                connected = true;
            } catch (IOException | TimeoutException e) {
                LOG.error(e.getMessage(), e);
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException ie) {
                    LOG.warn("Reconnection timeout interrupted: {}", ie.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Opens a connection to RabbitMQ.
     *
     * @throws IOException      IOException if the connection failed.
     * @throws TimeoutException TimeoutException if the connection timed out.
     */
    public void connect() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setUsername(config.getUsername());
        factory.setPassword(config.getPassword());
        LOG.info("Connecting to {}...", config.getHost());
        this.connection = factory.newConnection();

        this.channelTalk = connection.createChannel();
        this.channelListen = connection.createChannel();
        LOG.info("Successfully connected to {}...", config.getHost());
    }

    /**
     * Closes the connection to RabbitMQ.
     *
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() {
        try {
            channelTalk.close();
            channelListen.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
