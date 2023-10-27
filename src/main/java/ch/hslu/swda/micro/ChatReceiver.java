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

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChatReceiver implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(ChatReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;

    public ChatReceiver(final String exchangeName, final BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {

        // receive message and reply
        try {
            LOG.debug("received chat message with replyTo property [{}]: [{}]", replyTo, message);
            LOG.debug("sending answer with topic [{}] according to replyTo-property", replyTo);
            bus.reply(exchangeName, replyTo, corrId, "Hello there. This is the service template.");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

    }

}
