package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.security.User;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
public interface SecurityService {

    /**
     *
     * @param entity User entity
     * @param operation resetPassword operation
     * @param instanceId username
     * @return New password
     * @throws PMException
     */
    @Transactional
    public User resetPassword(Entity entity, Operation operation, String instanceId) throws PMException;

    public User authenticate(String username, String password) throws NotAuthorizedException;

    @Transactional
    public void changePassword(Entity entity, Operation operation, String instanceId, String current, String newpass) throws PMException;
}
