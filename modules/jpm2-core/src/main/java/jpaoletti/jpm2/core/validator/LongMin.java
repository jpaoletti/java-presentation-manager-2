package jpaoletti.jpm2.core.validator;

import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 *
 * @author jpaoletti
 */
public class LongMin implements FieldValidator {

    private Long min = Long.MIN_VALUE;
    private String message = "jpm.validator.too.small";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final Long value = (Long) convertedValue;
        if (value == null || value >= getMin()) {
            return null;
        } else {
            return MessageFactory.error(getMessage(), String.valueOf(convertedValue), String.valueOf(getMin()));
        }
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
