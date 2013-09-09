package jpaoletti.jpm2.core.service;

import java.util.List;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.core.model.Entity;

/**
 * Audit system.
 *
 * @author jpaoletti
 */
public interface AuditService {

    public void register(JPMContext context, String observations);

    public void register(JPMContext context);

    public List<AuditRecord> getItemRecords(Entity entity, String instanceId);

    public List<AuditRecord> getGeneralRecords(Entity entity);
}
