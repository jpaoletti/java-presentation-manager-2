package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
public interface SecurityService {

    /**
     *
     * @param entity User entity
     * @param context
     * @param operation resetPassword operation
     * @param instanceId username
     * @return New password
     * @throws PMException
     */
    @Transactional
    public UserDetails resetPassword(Entity entity, String context, Operation operation, String instanceId) throws PMException;

    @Transactional
    public void changePassword(Entity entity, String context, Operation operation, String instanceId, String current, String newpass) throws PMException;
}
