package jpaoletti.jpm2.core.service;

import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.SessionEntityData;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
public interface JPMService {

    public PaginatedList getWeakList(ContextualEntity centity, String instanceId, ContextualEntity weak) throws PMException;

    @Transactional
    public PaginatedList getPaginatedList(ContextualEntity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize, ContextualEntity owner, String ownerId) throws PMException;

    @Transactional(rollbackFor = Exception.class)
    public IdentifiedObject update(Entity entity, String context, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;

    @Transactional(rollbackFor = Exception.class)
    public IdentifiedObject delete(Entity entity, String context, Operation operation, String instanceId) throws PMException;

    /**
     * Retrieve an object of this entity, caring about the operation being
     * executed.
     */
    public IdentifiedObject get(Entity entity, String context, Operation operation, String instanceId) throws PMException;

    /**
     * Retrieve an object of this entity, without caring about the operation
     * being executed.
     */
    public IdentifiedObject get(Entity entity, String context, String instanceId) throws PMException;

    @Transactional(rollbackFor = Exception.class)
    public IdentifiedObject save(Entity entity, String context, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;

    @Transactional
    public IdentifiedObject save(Entity owner, String ownerId, Entity entity, String context, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;
}
