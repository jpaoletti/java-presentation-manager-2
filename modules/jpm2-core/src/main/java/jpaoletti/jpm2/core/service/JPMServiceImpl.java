package jpaoletti.jpm2.core.service;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.dao.GenericDAO;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.SessionEntityData;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Transactional
public class JPMServiceImpl extends JPMServiceBase implements JPMService {

    /**
     *
     * @param entity Owner entity
     * @param instanceId Owner id
     * @param weak Weak entity
     * @return Weak entity paginated list
     * @throws jpaoletti.jpm2.core.PMException
     */
    @Override
    public PaginatedList getWeakList(Entity entity, String instanceId, Entity weak) throws PMException {
        final Object owner = entity.getDao().get(instanceId);
        final List list = weak.getDao().list(new DAOListConfiguration(Restrictions.eq(weak.getOwner().getLocalProperty(), owner)));
        final PaginatedList pl = new PaginatedList();
        getContext().setEntity(entity);
        pl.setTotal((long) list.size());
        pl.getContents().load(list, weak, weak.getOperation("list"));
        return pl;
    }

    @Override
    public PaginatedList getPaginatedList(Entity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize, String ownerId) throws PMException {
        entity.checkAuthorization();
        operation.checkAuthorization();
        final DAOListConfiguration configuration = new DAOListConfiguration();
        final PaginatedList pl = new PaginatedList();
        final Criterion search = sessionEntityData.getSearchCriteria().getCriterion();
        if (ownerId != null) {
            final Object owner = entity.getOwner().getOwner().getDao().get(ownerId);
            configuration.getRestrictions().add(Restrictions.eq(entity.getOwner().getLocalProperty(), owner));
        }
        if (search != null) {
            configuration.getRestrictions().add(search);
            configuration.getAliases().putAll(sessionEntityData.getSearchCriteria().getAliases());
        }
        if (sessionEntityData.getSort().isSorted()) {
            configuration.setOrder(sessionEntityData.getSort().getOrder());
        }
        final GenericDAO dao = entity.getDao();
        if (entity.isPaginable()) {
            pl.setPageSize(pageSize != null ? pageSize : sessionEntityData.getPageSize());
            pl.setPage(page != null ? page : sessionEntityData.getPage());
            configuration.setFrom(pl.from());
            configuration.setMax(pl.getPageSize());
            if (entity.isCountable()) {
                pl.setTotal(dao.count(configuration));
            }
            sessionEntityData.setPage(pl.getPage());
            sessionEntityData.setPageSize(pl.getPageSize());
        }
        final List list = dao.list(configuration);
        pl.getContents().load(list, entity, operation);
        for (Field field : entity.getOrderedFields()) {
            if (field.getSearcher() != null) {
                pl.getFieldSearchs().put(field, field.getSearcher().visualization(field));
            }
        }
        //getJpm().audit(); //not for now
        return pl;
    }

    @Override
    public IdentifiedObject update(Entity entity, Operation operation, EntityInstance instance, Map<String, String[]> parameters) throws PMException {
        final String instanceId = instance.getIobject().getId();
        final Object object = entity.getDao().get(instanceId);
        instance.getIobject().setObject(object);
        processFields(entity, operation, object, instance, parameters);
        preExecute(operation, object);
        entity.getDao().update(object);
        postExecute(operation, object);
        getJpm().audit(entity, operation, instance.getIobject());
        return new IdentifiedObject(instanceId, object);
    }

    @Override
    public IdentifiedObject delete(Entity entity, Operation operation, String instanceId) throws PMException {
        final Object object = entity.getDao().get(instanceId); //current object
        preExecute(operation, object);
        entity.getDao().delete(object);
        postExecute(operation, object);
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        return iobject;
    }

    @Override
    public IdentifiedObject get(Entity entity, Operation operation, String instanceId) throws PMException {
        preExecute(operation, null);
        final Object object = entity.getDao().get(instanceId); //current object
        postExecute(operation, object);
        return new IdentifiedObject(instanceId, object);
    }

    @Override
    public IdentifiedObject get(Entity entity, String instanceId) throws PMException {
        return new IdentifiedObject(instanceId, entity.getDao().get(instanceId));
    }

    @Override
    public IdentifiedObject save(Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        final Object object = JPMUtils.newInstance(entity.getClazz());
        processFields(entity, operation, object, entityInstance, parameters);
        preExecute(operation, object);
        entity.getDao().save(object);
        postExecute(operation, object);
        final String instanceId = entity.getDao().getId(object).toString();
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        return iobject;
    }

    @Override
    public IdentifiedObject save(Entity owner, String ownerId, Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        final Object ownerObject = owner.getDao().get(ownerId);
        final Object object = JPMUtils.newInstance(entity.getClazz());
        JPMUtils.set(object, entity.getOwner().getLocalProperty(), ownerObject);
        processFields(entity, operation, object, entityInstance, parameters);
        preExecute(operation, object);
        entity.getDao().save(object);
        postExecute(operation, object);
        final String instanceId = entity.getDao().getId(object).toString();
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        return iobject;
    }
}
