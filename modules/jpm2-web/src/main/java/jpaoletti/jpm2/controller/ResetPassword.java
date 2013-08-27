package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.UUID;
import static jpaoletti.jpm2.controller.OperationAction.COMMIT_ERROR;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.core.security.BCrypt;
import jpaoletti.jpm2.core.security.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reset a user password and generates a new one.
 *
 * @author jpaoletti
 */
public class ResetPassword extends OperationAction {

    private String value;

    public ResetPassword() {
        this.requireObject = true;
    }

    @Override
    @Transactional
    public String execute() throws Exception {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            try {
                preExecute();
            } catch (ValidationException e) {
                getEntityMessages().add(e.getMsg());
                return COMMIT_ERROR;
            }
            this.value = UUID.randomUUID().toString().substring(0, 8);;
            final User user = (User) getObject();
            user.setPassword(BCrypt.hashpw(getValue(), BCrypt.gensalt()));
            getEntity().getDao().update(user);
            postExecute();
            return SUCCESS;
        } else {
            return prepare;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
