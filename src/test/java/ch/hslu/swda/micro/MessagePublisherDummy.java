package ch.hslu.swda.micro;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Dummy implementation of the message publisher used for testing.
 */
public class MessagePublisherDummy<T> implements MessagePublisher<T> {

    @Override
    public String sendMessage(final String route, final T messageObject) {
        String message = "";
        try {
            message = new ObjectMapper().writeValueAsString(messageObject);
        } catch (JsonProcessingException e) {
            message = "Failed to serialize message: {}" + e.getMessage();
        }
        return message;
    }
}
