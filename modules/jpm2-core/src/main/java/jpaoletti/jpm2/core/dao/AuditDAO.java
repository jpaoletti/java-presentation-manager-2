package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.model.AuditRecord;

/**
 *
 * @author jpaoletti
 */
public class AuditDAO extends GenericDAO<AuditRecord, Long> {

    @Override
    public Long getId(Object object) {
        return ((AuditRecord) object).getId();
    }
}
