package jpaoletti.jpm2.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.message.Message;

/**
 * The result for a post request in the form of json.
 *
 * @author jpaoletti
 */
public class JPMPostResponse {

    private final boolean ok;
    private final List<Message> messages = new ArrayList<>();
    private final Map<String, List<Message>> fieldMessages = new LinkedHashMap<>();
    private final String next;

    public JPMPostResponse(boolean ok, String next, Message... messages) {
        this.ok = ok;
        this.next = next.replaceAll("redirect:", "");
        this.messages.addAll(Arrays.asList(messages));
    }

    public JPMPostResponse(boolean ok, String next, List<Message> messages) {
        this.ok = ok;
        this.next = next;
        this.messages.addAll(messages);
    }

    public JPMPostResponse(boolean ok, String next, List<Message> messages, Map<String, List<Message>> fieldMessages) {
        this.ok = ok;
        this.next = next;
        this.messages.addAll(messages);
        this.fieldMessages.putAll(fieldMessages);
    }

    public String getNext() {
        return next;
    }

    public Map<String, List<Message>> getFieldMessages() {
        return fieldMessages;
    }

    public boolean isOk() {
        return ok;
    }

    public List<Message> getMessages() {
        return messages;
    }

}
