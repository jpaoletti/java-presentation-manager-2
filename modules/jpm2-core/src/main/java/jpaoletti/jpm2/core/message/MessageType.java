package jpaoletti.jpm2.core.message;

import java.io.Serializable;

/**
 * Message types: error, warning, information.
 *
 * @author jpaoletti
 * @since 13/09/2011
 * @version v1.1
 *
 */
public enum MessageType implements Serializable {

    ERROR("error"), WARN("warn"), INFO("info");
    private String name;

    private MessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
