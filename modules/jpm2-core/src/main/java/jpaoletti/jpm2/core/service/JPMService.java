package jpaoletti.jpm2.core.service;

import java.util.Map;
import jpaoletti.jpm2.core.PMException;
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

    public PaginatedList getWeakList(Entity entity, String instanceId, Entity weak) throws PMException;

    @Transactional
    public PaginatedList getPaginatedList(Entity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize, String ownerId) throws PMException;

    @Transactional
    public IdentifiedObject update(Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;

    @Transactional
    public IdentifiedObject delete(Entity entity, Operation operation, String instanceId) throws PMException;

    /**
     * Retrieve an object of this entity, caring about the operation being
     * executed.
     */
    public IdentifiedObject get(Entity entity, Operation operation, String instanceId) throws PMException;

    /**
     * Retrieve an object of this entity, without caring about the operation
     * being executed.
     */
    public IdentifiedObject get(Entity entity, String instanceId) throws PMException;

    @Transactional
    public IdentifiedObject save(Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;

    @Transactional
    public IdentifiedObject save(Entity owner, String ownerId, Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;
}
