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
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
 * Beispielcode für Verbindung mit RabbitMQ.
 */
public final class BusConnector implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(BusConnector.class);

    // connection to bus
    private Connection connection;

    // use different channels for different threads
    private Channel channelTalk;
    private Channel channelListen;

    /**
     * Beispiel für asynchrone Kommunikation (Send).
     *
     * @param exchange Exchange.
     * @param route    Route.
     * @param message  Message.
     * @throws IOException Exception.
     */
    public void talkAsync(final String exchange, final String route, final String message) throws IOException {
        AMQP.BasicProperties props = new AMQP.BasicProperties();
        channelTalk.basicPublish(exchange, route, props, message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Beispiel für Beantwortung einer synchronen Kommunikation (Send).
     *
     * @param exchange Exchange.
     * @param route    Route.
     * @param corrId   Route.
     * @param message  Message.
     * @throws IOException Exception.
     */
    public void reply(final String exchange, final String route, final String corrId, final String message) throws IOException {
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId).build();
        channelTalk.basicPublish(exchange, route, props, message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Beispiel für synchrone Kommunikation.
     *
     * @param exchange Exchange.
     * @param route    Route.
     * @param message  Message.
     * @return String.
     * @throws IOException          Exception.
     * @throws InterruptedException Exception.
     */
    public String talkSync(final String exchange, final String route, final String message)
            throws IOException, InterruptedException {

        // create a temporary reply queue
        final String corrId = UUID.randomUUID().toString();
        final String replyQueueName = channelTalk.queueDeclare().getQueue();
        channelTalk.queueBind(replyQueueName, exchange, replyQueueName);

        // setup receiver
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        final String consumerId = channelTalk.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {

            // check if response matches correlation id
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
            }
        }, consumerTag -> {
            // empty
        });

        // send message
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName)
                .build();
        channelTalk.basicPublish(exchange, route, props, message.getBytes(StandardCharsets.UTF_8));

        // To receive a message without timeout, use:
        // String result = response.take();

        // receive message with timeout
        final String result = response.poll(5, TimeUnit.SECONDS);
        channelTalk.basicCancel(consumerId);
        return result;

    }

    /**
     * Beispiel für Listener (asynchroner Empfang).
     *
     * @param exchange  Exchange.
     * @param queueName Queue.
     * @param route     Route.
     * @param receiver  Empfänger.
     * @throws IOException IOException.
     */
    public void listenFor(final String exchange, final String queueName, final String route,
                          final MessageReceiver receiver) throws IOException {

        // create queue to receive messages
        channelListen.queueDeclare(queueName, true, false, false, new HashMap<>());
        channelListen.queueBind(queueName, exchange, route);

        // add listener
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            receiver.onMessageReceived(route, delivery.getProperties().getReplyTo(), delivery.getProperties().getCorrelationId(), message);
        };
        channelListen.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            // empty
        });
    }

    /**
     * Öffnet die Verbindung zu RabbitMQ.
     *
     * @throws IOException      IOException.
     * @throws TimeoutException TimeoutException.
     */
    public void connect() throws IOException, TimeoutException {

        // retrieve configuration
        RabbitMqConfig config = new RabbitMqConfig();

        // create connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setUsername(config.getUsername());
        factory.setPassword(config.getPassword());
        LOG.debug("Connecting to {}...", config.getHost());
        this.connection = factory.newConnection();

        // create channels within connection
        this.channelTalk = connection.createChannel();
        this.channelListen = connection.createChannel();
        LOG.debug("Successfully connected to {}...", config.getHost());
    }

    /**
     * Schliesst die Verbindung zu RabbitMQ.
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
