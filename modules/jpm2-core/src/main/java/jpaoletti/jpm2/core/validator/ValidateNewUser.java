package jpaoletti.jpm2.core.validator;

import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.OperationValidator;
import jpaoletti.jpm2.core.model.ValidationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author jpaoletti
 */
public class ValidateNewUser extends PMCoreObject implements OperationValidator {

    @Override
    public void validate(Object object) throws ValidationException {
        try {
            final UserDetails user = (UserDetails) object;
            if (getJpm().getEntity("jpm-entity-user").getDao().get(user.getUsername()) != null) {
                throw new ValidationException(MessageFactory.error("jpm.validator.userexists", user.getUsername()));
            }
            if (user.getUsername().contains(" ")) {
                throw new ValidationException(MessageFactory.error("jpm.validator.userwithspaces", user.getUsername()));
            }
        } catch (EntityNotFoundException ex) {
            throw new ValidationException(MessageFactory.error(ex.getMessage()));
        }
    }
}
