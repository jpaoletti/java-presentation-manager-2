package jpaoletti.jpm2.core.validator;

import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 *
 * @author jpaoletti
 */
public class IntegerMin implements FieldValidator {

    private Integer min = Integer.MIN_VALUE;
    private String message = "jpm.validator.too.small";

    @Override
    public Message validate(Object object, Object convertedValue) {
        final Integer value = (Integer) convertedValue;
        if (value == null || value >= getMin()) {
            return null;
        } else {
            return MessageFactory.error(getMessage(), String.valueOf(convertedValue), String.valueOf(getMin()));
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
