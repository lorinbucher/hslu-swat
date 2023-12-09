package ch.hslu.swda.micro;

import java.util.function.Consumer;

/**
 * Message listener.
 */
public interface MessageListener {

    /**
     * Receives messages from the message queue.
     *
     * @param route    Message queue.
     * @param callback Message processing callback.
     */
    void receiveMessages(String route, Consumer<String> callback);

}
