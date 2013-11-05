package jpaoletti.jpm2.core.validator;

import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 *
 * @author jpaoletti
 */
public class StringMin implements FieldValidator {

    private Integer min = 0;
    private String message = "jpm.validator.too.short";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final String value = (String) convertedValue;
        if (value == null || value.length() >= getMin()) {
            return null;
        } else {
            return MessageFactory.error(getMessage(), String.valueOf(value.length()), String.valueOf(getMin()));
        }
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
