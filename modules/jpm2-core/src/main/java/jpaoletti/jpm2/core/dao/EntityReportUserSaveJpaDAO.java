package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.model.reports.EntityReportUserSave;

/**
 *
 * @author jpaoletti
 */
public class EntityReportUserSaveJpaDAO extends JPADAO<EntityReportUserSave, Long> {

    @Override
    public Long getId(Object object) {
        return ((EntityReportUserSave) object).getId();
    }
}
