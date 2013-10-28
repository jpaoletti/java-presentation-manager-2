package jpaoletti.jpm2.core.message;

import java.io.Serializable;

/**
 * Message types: success, error, warning, information.
 *
 * @author jpaoletti
 * @since 13/09/2011
 * @version v1.1
 *
 */
public enum MessageType implements Serializable {

    SUCCESS("success"), ERROR("danger"), WARN("warning"), INFO("info");
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
