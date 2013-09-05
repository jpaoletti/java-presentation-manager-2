package jpaoletti.jpm2.core.service;

import java.util.Date;
import java.util.List;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.dao.AuditDAO;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author jpaoletti
 */
@Service
public class AuditServiceDatabase extends PMCoreObject implements AuditService {

    @Autowired
    private JPMContext context;
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    private AuditDAO dao;

    @Override
    public void register(String observations) {
        if (getContext().getEntity() != null && !getContext().getEntity().isAuditable()) {
            return;
        }
        if (getContext().getOperation() != null && !getContext().getOperation().isAuditable()) {
            return;
        }
        try {
            final AuditRecord record = new AuditRecord();
            record.setDatetime(new Date());
            if (getAuthentication() != null && getAuthentication().getPrincipal() != null) {
                record.setUsername((getUserDetails()).getUsername());
            }

            if (getContext().getEntity() != null) {
                record.setEntity(getContext().getEntity().getId());
                if (getContext().getObject() != null) {
                    record.setItem(getContext().getEntity().getDao().getId(getContext().getObject()).toString());
                }
            }

            if (getContext().getOperation() != null) {
                record.setOperation(getContext().getOperation().getId());
            }

            if (observations != null) {
                record.setObservations(observations);
            } else {
                if (getContext().getObject() != null) {
                    record.setObservations(getContext().getObject().toString());
                }
            }
            getSessionFactory().getCurrentSession().save(record);
        } catch (Exception ex) {
            JPMUtils.getLogger().error(ex);
        }
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    @Override
    public void register() {
        register("");
    }

    @Override
    public List<AuditRecord> getItemRecords(String instanceId) {
        return getDao().list(
                Order.desc("id"),
                Restrictions.eq("entity", getContext().getEntity().getId()),
                Restrictions.eq("item", instanceId));
    }

    @Override
    public List<AuditRecord> getGeneralRecords() {
        return getDao().list(
                Order.desc("id"),
                Restrictions.eq("entity", getContext().getEntity().getId()),
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
