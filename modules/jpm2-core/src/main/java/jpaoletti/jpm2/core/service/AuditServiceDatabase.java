package jpaoletti.jpm2.core.service;

import java.util.Date;
import java.util.List;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.dao.AuditDAO;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Transactional
public class AuditServiceDatabase extends PMCoreObject implements AuditService {

    @Autowired(required = false)
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired(required = false)
    private AuditDAO dao;

    @Override
    public void register(Entity entity, String operation, IdentifiedObject iobject, String observations) {
        try {
            final AuditRecord record = buildRecord(entity, operation, iobject, observations);
            if (getSessionFactory() != null && record != null) {
                getSessionFactory().getCurrentSession().save(record);
            }
        } catch (Exception ex) {
            JPMUtils.getLogger().error(ex);
        }
    }

    @Override
    public AuditRecord buildRecord(Entity entity, String operation, IdentifiedObject iobject, String observations) {
        if (entity != null && !entity.isAuditable()) {
            return null;
        }
//        if (operation != null && !operation.isAuditable()) {
//            return null;
//        }
        final AuditRecord record = new AuditRecord();
        record.setDatetime(new Date());
        if (getAuthorizationService().getCurrentUsername() != null) {
            record.setUsername(getAuthorizationService().getCurrentUsername());
        }
        if (entity != null) {
            record.setEntity(entity.getAuditId());
            if (iobject != null) {
                record.setItem(iobject.getId());
            }
        }
        if (operation != null) {
            record.setOperation(operation);
        }
        if (observations != null && !observations.equals("")) {
            record.setObservations(observations);
        } else if (iobject != null) {
            record.setObservations(String.valueOf(iobject.getObject()));
        }
        return record;
    }

    @Override
    public void register(Entity entity, String operation, IdentifiedObject iobject) {
        register(entity, operation, iobject, null);
    }

    @Override
    public List<AuditRecord> getItemRecords(Entity entity, String instanceId) {
        return getDao().list(new DAOListConfiguration(
                Order.desc("id"),
                Restrictions.eq("entity", entity.getAuditId()),
                Restrictions.eq("item", instanceId)));
    }

    @Override
    public List<AuditRecord> getGeneralRecords(Entity entity) {
        return getDao().list(new DAOListConfiguration(
                Order.desc("id"),
                Restrictions.eq("entity", entity.getAuditId()),
                Restrictions.or(Restrictions.isNull("item"), Restrictions.eq("operation", "delete"))));
    }

    public AuditDAO getDao() {
        return dao;
    }

    public void setDao(AuditDAO dao) {
        this.dao = dao;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
