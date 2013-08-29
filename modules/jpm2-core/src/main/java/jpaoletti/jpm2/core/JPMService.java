package jpaoletti.jpm2.core;

import java.util.Map;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.SessionEntityData;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
public interface JPMService {

    @Transactional(readOnly = true)
    public PaginatedList getPaginatedList(Entity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize) throws PMException;

    @Transactional
    public Object update(Entity entity, Operation operation, String instanceId, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;

    @Transactional
    public void delete(Entity entity, Operation operation, String instanceId) throws PMException;

    @Transactional
    public String save(Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException;
}
