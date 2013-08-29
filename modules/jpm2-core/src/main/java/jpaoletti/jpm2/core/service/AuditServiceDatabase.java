package jpaoletti.jpm2.core.service;

import java.util.Date;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.SessionFactory;
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
            }

            if (getContext().getOperation() != null) {
                record.setOperation(getContext().getOperation().getId());
            }

            if (getContext().getObject() != null) {
                record.setItem(getContext().getEntity().getDao().getId(getContext().getObject()).toString());
            }
            record.setObservations(observations);
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

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void register() {
        register("");
    }
}
