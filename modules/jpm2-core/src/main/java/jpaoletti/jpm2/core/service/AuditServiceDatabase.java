package jpaoletti.jpm2.core.service;

import java.util.Date;
import java.util.List;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.dao.AuditJpaDAO;
import jpaoletti.jpm2.core.dao.DAOOrder;
import jpaoletti.jpm2.core.dao.JPADAOListConfiguration;
import jpaoletti.jpm2.core.model.AuditRecord;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityOwner;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Transactional
public class AuditServiceDatabase extends PMCoreObject implements AuditService {

    @Autowired(required = false)
    private AuditJpaDAO dao;

    @Override
    public void register(Entity entity, String operation, IdentifiedObject iobject, String observations) {
        try {
            final AuditRecord record = buildRecord(entity, operation, iobject, observations);
            if (getDao() != null && record != null) {
                getDao().save(record);
            }
        } catch (Exception ex) {
            JPMUtils.getLogger().error(ex);
        }
    }

    @Override
    public void register(Entity entity, String operation, IdentifiedObject iobject, String observations, String username) {
        try {
            final AuditRecord record = buildRecord(entity, operation, iobject, observations);
            if (record != null && StringUtils.isNotEmpty(username)) {
                record.setUsername(username);
            }
            if (getDao() != null && record != null) {
                getDao().save(record);
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
                record.setItemOwner(resolveItemOwner(entity, iobject));
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
        final JPADAOListConfiguration cfg = getDao().build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("entity"), entity.getAuditId()));
        cfg.withPredicate((cb, root) -> cb.equal(root.get("item"), instanceId));
        cfg.withOrder(new DAOOrder("id", false));
        return getDao().list(cfg);
    }

    @Override
    public List<AuditRecord> getGeneralRecords(Entity entity) {
        final JPADAOListConfiguration cfg = getDao().build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("entity"), entity.getAuditId()));
        cfg.withPredicate((cb, root) -> cb.or(cb.isNull(root.get("item")), cb.equal(root.get("operation"), "delete")));
        cfg.withOrder(new DAOOrder("id", false));
        return getDao().list(cfg);
    }

    @Override
    public List<AuditRecord> getGeneralRecords(Entity entity, String ownerId) {
        if (StringUtils.isBlank(ownerId) || entity == null || entity.getOwner() == null) {
            return getGeneralRecords(entity);
        }
        final JPADAOListConfiguration cfg = getDao().build();
        cfg.withPredicate((cb, root) -> cb.equal(root.get("entity"), entity.getAuditId()));
        cfg.withPredicate((cb, root) -> cb.equal(root.get("itemOwner"), ownerId));
        cfg.withOrder(new DAOOrder("id", false));
        return getDao().list(cfg);
    }

    private String resolveItemOwner(Entity entity, IdentifiedObject iobject) {
        final EntityOwner entityOwner = entity.getOwner();
        if (entityOwner == null || iobject.getObject() == null) {
            return null;
        }
        try {
            final Object ownerObject = entityOwner.getOwnerObject(null, iobject.getObject());
            if (ownerObject == null) {
                return null;
            }
            if (entityOwner.isOnlyId()) {
                return String.valueOf(ownerObject);
            }
            if (entityOwner.getOwner() == null) {
                return null;
            }
            final Object ownerId = entityOwner.getOwner().getDao(null).getId(ownerObject);
            return ownerId != null ? String.valueOf(ownerId) : null;
        } catch (Exception ex) {
            JPMUtils.getLogger().warn("Error resolving audit owner for entity " + entity.getId(), ex);
            return null;
        }
    }

    public AuditJpaDAO getDao() {
        return dao;
    }

    public void setDao(AuditJpaDAO dao) {
        this.dao = dao;
    }
}
