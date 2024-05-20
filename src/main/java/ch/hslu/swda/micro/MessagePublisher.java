package ch.hslu.swda.micro;

/**
 * Message publisher.
 *
 * @param <T> Message DTO.
 */
public interface MessagePublisher<T> {

    /**
     * Sends a message object to the message queue.
     *
     * @param route         Message queue.
     * @param messageObject Message object.
     */
    void sendMessage(String route, T messageObject);

}
