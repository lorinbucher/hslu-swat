package ch.hslu.swda.micro;

/**
 * Message publisher.
 */
public interface MessagePublisher<T> {

    /**
     * Sends a message object to the message queue.
     *
     * @param route         Message queue.
     * @param messageObject Message object.
     * @return Sent message.
     */
    public String sendMessage(String route, T messageObject);

}
