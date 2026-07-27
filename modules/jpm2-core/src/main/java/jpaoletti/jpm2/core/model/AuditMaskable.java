package jpaoletti.jpm2.core.model;

/**
 * Opt-in contract for entities that carry sensitive fields which must never appear in clear
 * in the detailed-audit diff. Implemented by the entity object itself so masking can depend
 * on the row's own state (e.g. a {@code Sysparam} masks its {@code value} field only when it
 * is of a secret type). Honored by {@code JPMUtils.buildAuditDiff}.
 *
 * @author jpaoletti
 */
public interface AuditMaskable {

    /**
     * @param fieldId the field being rendered in the audit diff
     * @return true if this field's value must be masked (shown as bullets, never in clear)
     * for this particular instance
     */
    boolean maskInAudit(String fieldId);
}
