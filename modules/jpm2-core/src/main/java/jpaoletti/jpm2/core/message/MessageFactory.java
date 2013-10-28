package jpaoletti.jpm2.core.message;

/**
 * Factory for messages
 *
 * @author jpaoletti
 * @since 13/09/2011
 * @version v1.1
 *
 */
public class MessageFactory {

    /**
     * Create an information message
     *
     * @param key Message key
     * @param args Message arguments
     * @return a new Message of type info with the given key and arguments
     */
    public static Message info(String key, String... args) {
        return message(MessageType.INFO, key, args);
    }

    /**
     * Create a warning message
     *
     * @param key Message key
     * @param args Message arguments
     * @return a new Message of type warning with the given key and arguments
     */
    public static Message warn(String key, String... args) {
        return message(MessageType.WARN, key, args);
    }

    /**
     * Create an error message
     *
     * @param key Message key
     * @param args Message arguments
     * @return a new Message of type error with the given key and arguments
     */
    public static Message error(String key, String... args) {
        return message(MessageType.ERROR, key, args);
    }

    /**
     * Create success message
     *
     * @param key Message key
     * @param args Message arguments
     * @return a new Message of type error with the given key and arguments
     */
    public static Message success(String key, String... args) {
        return message(MessageType.SUCCESS, key, args);
    }

    private static Message message(MessageType type, String key, String... args) {
        final Message message = new Message();
        message.setType(type);
        message.setKey(key);
        message.setArgs(args);
        return message;
    }

}
