package jpaoletti.jpm2.core.service;

/**
 * Audit system.
 *
 * @author jpaoletti
 */
public interface AuditService {

    public void register(String observations);

    public void register();
}
