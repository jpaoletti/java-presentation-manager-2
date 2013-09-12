package jpaoletti.jpm2.core.service;

import java.util.List;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;

/**
 * Audit system.
 *
 * @author jpaoletti
 */
public interface AuditService {

    public void register(Entity entity, Operation operation, IdentifiedObject iobject, String observations);

    public void register(Entity entity, Operation operation, IdentifiedObject iobject);

    public List<AuditRecord> getItemRecords(Entity entity, String instanceId);

    public List<AuditRecord> getGeneralRecords(Entity entity);
}
