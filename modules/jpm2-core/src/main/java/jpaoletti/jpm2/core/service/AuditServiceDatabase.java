package jpaoletti.jpm2.core.service;

import java.util.Date;
import java.util.List;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.dao.AuditDAO;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
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
    public void register(Entity entity, Operation operation, IdentifiedObject iobject, String observations) {
        if (entity != null && !entity.isAuditable()) {
            return;
        }
        if (operation != null && !operation.isAuditable()) {
            return;
        }
        try {
            final AuditRecord record = new AuditRecord();
            record.setDatetime(new Date());
            if (getAuthentication() != null && getAuthentication().getPrincipal() != null) {
                record.setUsername((getUserDetails()).getUsername());
            }
            if (entity != null) {
                record.setEntity(entity.getId());
                if (iobject != null) {
                    record.setItem(iobject.getId());
                }
            }

            if (operation != null) {
                record.setOperation(operation.getId());
            }

            if (observations != null && !observations.equals("")) {
                record.setObservations(observations);
            } else {
                if (iobject != null) {
                    record.setObservations(String.valueOf(iobject.getObject()));
                }
            }
            if (getSessionFactory() != null) {
                getSessionFactory().getCurrentSession().save(record);
            }
        } catch (Exception ex) {
            JPMUtils.getLogger().error(ex);
        }
    }

    @Override
    public void register(Entity entity, Operation operation, IdentifiedObject iobject) {
        register(entity, operation, iobject, null);
    }

    @Override
    public List<AuditRecord> getItemRecords(Entity entity, String instanceId) {
        return getDao().list(
                Order.desc("id"),
                Restrictions.eq("entity", entity.getId()),
                Restrictions.eq("item", instanceId));
    }

    @Override
    public List<AuditRecord> getGeneralRecords(Entity entity) {
        return getDao().list(
                Order.desc("id"),
                Restrictions.eq("entity", entity.getId()),
                Restrictions.or(Restrictions.isNull("item"), Restrictions.eq("operation", "delete")));
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
