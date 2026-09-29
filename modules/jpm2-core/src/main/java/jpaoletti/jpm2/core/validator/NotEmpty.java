package jpaoletti.jpm2.core.validator;

import java.util.Collection;
import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 *
 * @author jpaoletti
 */
public class NotEmpty implements FieldValidator {

    private String message = "jpm.validator.not.empty";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final boolean empty;
        if (convertedValue == null) {
            empty = true;
        } else if (convertedValue instanceof Collection) {
            empty = ((Collection) convertedValue).isEmpty();
        } else if (convertedValue instanceof Map) {
            empty = ((Map) convertedValue).isEmpty();
        } else if (convertedValue.getClass().isArray()) {
            empty = java.lang.reflect.Array.getLength(convertedValue) == 0;
        } else {
            empty = convertedValue.toString().trim().isEmpty();
        }
        return empty ? MessageFactory.error(getMessage()) : null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
