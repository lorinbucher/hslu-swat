package ch.hslu.swda.micro;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Dummy implementation of the message listener used for testing.
 */
public final class MessageListenerDummy implements MessageListener {

    private final Map<String, Consumer<String>> callbacks = new HashMap<>();

    @Override
    public void receiveMessages(final String route, final Consumer<String> callback) {
        callbacks.put(route, callback);
    }

    public void mockMessage(final String route, final String message) {
        Consumer<String> callback = callbacks.get(route);
        if (callback != null) {
            callback.accept(message);
        }
    }
}
