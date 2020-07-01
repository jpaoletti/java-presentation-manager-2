package jpaoletti.jpm2.core.validator;

import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 *
 * @author jpaoletti
 */
public class LongMax implements FieldValidator {

    private Long max = Long.MAX_VALUE;
    private String message = "jpm.validator.too.big";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final Long value = (Long) convertedValue;
        if (value == null || value <= getMax()) {
            return null;
        } else {
            return MessageFactory.error(getMessage(), String.valueOf(convertedValue), String.valueOf(getMax()));
        }
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
