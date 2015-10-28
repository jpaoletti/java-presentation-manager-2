package jpaoletti.jpm2.core.service;

import java.util.UUID;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.security.BCrypt;
import jpaoletti.jpm2.core.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Transactional
public class SecurityServiceImpl extends JPMServiceBase implements SecurityService {

    @Autowired
    private BCrypt encoder;

    @Override
    public UserDetails resetPassword(Entity entity, String context, Operation operation, String instanceId) throws PMException {
        final String value = UUID.randomUUID().toString().substring(0, 8);
        final User user = (User) entity.getDao(context).get(instanceId);
        user.setPassword(BCrypt.hashpw(value, BCrypt.gensalt()));
        user.setNewPassword(value);
        preExecute(operation, user);
        entity.getDao(context).update(user);
        postExecute(operation, user);
        getJpm().audit(entity, operation, new IdentifiedObject(instanceId, user));
        return user;
    }

    @Override
    public void changePassword(Entity entity, String context, Operation operation, String instanceId, String current, String newpass) throws PMException {
        final User user = (User) entity.getDao(context).get(instanceId);
        if (!getEncoder().matches(current, user.getPassword())) {
            throw new NotAuthorizedException();
        }
        if (newpass == null || "".equals(newpass.trim())) {
            throw new PMException("jpm.profile.newpass.invalid");
        }
        user.setPassword(BCrypt.hashpw(newpass, BCrypt.gensalt()));
        preExecute(operation, user);
        entity.getDao(context).update(user);
        postExecute(operation, user);
        getJpm().audit(entity, operation, new IdentifiedObject(instanceId, user));
    }

    public BCrypt getEncoder() {
        return encoder;
    }

    public void setEncoder(BCrypt encoder) {
        this.encoder = encoder;
    }
}
