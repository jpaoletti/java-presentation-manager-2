package jpaoletti.jpm2.core.validator;

import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.FieldValidator;

/**
 * Composite validator that executes multiple validators in sequence.
 * Stops at the first validation error and returns that error message.
 *
 * @author jpaoletti
 */
public class CompositeFieldValidator implements FieldValidator {

    private List<FieldValidator> validators = new ArrayList<>();

    @Override
    public Message validate(Object object, Object convertedValue) {
        if (validators == null || validators.isEmpty()) {
            return null; // No validators, no validation
        }

        // Execute each validator in sequence
        for (FieldValidator validator : validators) {
            Message result = validator.validate(object, convertedValue);
            if (result != null) {
                // First error found, return it
                return result;
            }
        }

        // All validators passed
        return null;
    }

    public List<FieldValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<FieldValidator> validators) {
        this.validators = validators;
    }
}
