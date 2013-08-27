package jpaoletti.jpm2.core.validator;

import jpaoletti.jpm2.core.dao.UserDAO;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.OperationValidator;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.core.security.User;

/**
 *
 * @author jpaoletti
 */
public class ValidateNewUser implements OperationValidator {

    private UserDAO dao;

    @Override
    public void validate(Object object) throws ValidationException {
        final User user = (User) object;
        if (getDao().get(user.getUsername()) != null) {
            throw new ValidationException(MessageFactory.error("jpm.validator.userexists", user.getUsername()));
        }
    }

    public UserDAO getDao() {
        return dao;
    }

    public void setDao(UserDAO dao) {
        this.dao = dao;
    }
}
