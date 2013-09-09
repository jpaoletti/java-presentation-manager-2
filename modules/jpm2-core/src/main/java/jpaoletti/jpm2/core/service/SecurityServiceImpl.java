package jpaoletti.jpm2.core.service;

import java.util.UUID;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.security.BCrypt;
import jpaoletti.jpm2.core.security.User;

/**
 *
 * @author jpaoletti
 */
public final class SecurityServiceImpl extends JPMServiceBase implements SecurityService {

    @Override
    public User resetPassword(Entity entity, Operation operation, String instanceId) throws PMException {
        final String value = UUID.randomUUID().toString().substring(0, 8);
        final User user = (User) entity.getDao().get(instanceId);
        user.setPassword(BCrypt.hashpw(value, BCrypt.gensalt()));
        user.setNewPassword(value);
        preExecute(operation, user);
        entity.getDao().update(user);
        postExecute(operation);
        getJpm().audit(getContext());
        return user;
    }
}
