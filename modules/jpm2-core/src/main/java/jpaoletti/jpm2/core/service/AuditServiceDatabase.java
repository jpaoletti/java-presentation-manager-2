package jpaoletti.jpm2.core.service;

import java.util.Date;
import java.util.List;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.dao.AuditDAO;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author jpaoletti
 */
public class AuditServiceDatabase extends PMCoreObject implements AuditService {

    @Autowired(required = false)
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;
    @Autowired(required = false)
    private AuditDAO dao;

    @Override
    public void register(JPMContext context, String observations) {
        if (context.getEntity() != null && !context.getEntity().isAuditable()) {
            return;
        }
        if (context.getOperation() != null && !context.getOperation().isAuditable()) {
            return;
        }
        try {
            final AuditRecord record = new AuditRecord();
            record.setDatetime(new Date());
            if (getAuthentication() != null && getAuthentication().getPrincipal() != null) {
                record.setUsername((getUserDetails()).getUsername());
            }

            if (context.getEntity() != null) {
                record.setEntity(context.getEntity().getId());
                if (context.getObject() != null) {
                    record.setItem(context.getEntity().getDao().getId(context.getObject()).toString());
                }
            }

            if (context.getOperation() != null) {
                record.setOperation(context.getOperation().getId());
            }

            if (observations != null && !observations.equals("")) {
                record.setObservations(observations);
            } else {
                if (context.getObject() != null) {
                    record.setObservations(context.getObject().toString());
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
    public void register(JPMContext context) {
        register(context, null);
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
