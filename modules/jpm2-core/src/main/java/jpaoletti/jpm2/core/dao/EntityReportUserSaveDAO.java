package jpaoletti.jpm2.core.dao;

import jpaoletti.jpm2.core.model.reports.EntityReportUserSave;

/**
 *
 * @author jpaoletti
 */
public class EntityReportUserSaveDAO extends GenericDAO<EntityReportUserSave, String> {

    @Override
    public String getId(Object object) {
        return Long.toString(((EntityReportUserSave) object).getId());
    }
}
