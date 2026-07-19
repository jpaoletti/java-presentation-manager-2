package jpaoletti.jpm2.core.service;

import java.util.List;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.IdentifiedObject;

/**
 * Audit system.
 *
 * @author jpaoletti
 */
public interface AuditService {

    public void register(Entity entity, String operation, IdentifiedObject iobject, String observations);

    /**
     * Same as {@link #register(Entity, String, IdentifiedObject, String)} but
     * with an explicit username. Useful for contexts without a Spring Security
     * authentication (e.g. token-based APIs), where the acting user is known by
     * the caller but not present in the SecurityContext. When {@code username}
     * is null/empty the current authenticated username (if any) is used.
     */
    public void register(Entity entity, String operation, IdentifiedObject iobject, String observations, String username);

    public void register(Entity entity, String operation, IdentifiedObject iobject);

    public AuditRecord buildRecord(Entity entity, String operation, IdentifiedObject iobject, String observations);

    public List<AuditRecord> getItemRecords(Entity entity, String instanceId);

    public List<AuditRecord> getGeneralRecords(Entity entity);

    public List<AuditRecord> getGeneralRecords(Entity entity, String ownerId);
}
