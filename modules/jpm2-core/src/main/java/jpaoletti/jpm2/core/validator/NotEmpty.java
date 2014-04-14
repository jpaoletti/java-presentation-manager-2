package jpaoletti.jpm2.core.validator;

import java.util.Collection;
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
        if (convertedValue != null && !((Collection) convertedValue).isEmpty()) {
            return null;
        } else {
            return MessageFactory.error(getMessage());
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
