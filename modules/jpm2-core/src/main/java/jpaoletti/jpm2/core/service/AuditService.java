package jpaoletti.jpm2.core.service;

import java.util.List;
import jpaoletti.jpm2.core.model.AuditRecord;

/**
 * Audit system.
 *
 * @author jpaoletti
 */
public interface AuditService {

    public void register(String observations);

    public void register();

    public List<AuditRecord> getItemRecords(String instanceId);

    public List<AuditRecord> getGeneralRecords();
}
