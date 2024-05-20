package ch.hslu.swda.micro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Dummy implementation of the message publisher used for testing.
 *
 * @param <T> The message DTO.
 */
public final class MessagePublisherDummy<T> implements MessagePublisher<T> {

    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void sendMessage(final String route, final T messageObject) {
        try {
            messages.put(route, new ObjectMapper().writeValueAsString(messageObject));
        } catch (JsonProcessingException e) {
            messages.put(route, "Failed to serialize message: {}" + e.getMessage());
        }
    }

    public String getMessage(final String route) {
        return messages.get(route);
    }
}
