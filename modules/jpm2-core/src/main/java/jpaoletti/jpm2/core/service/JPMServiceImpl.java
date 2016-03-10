package jpaoletti.jpm2.core.service;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DAO;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.ListSort;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.SessionEntityData;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Transactional
public class JPMServiceImpl extends JPMServiceBase implements JPMService {

    @Override
    public PaginatedList getWeakList(ContextualEntity entity, String instanceId, ContextualEntity weak) throws PMException {
        final Object owner = entity.getDao().get(instanceId);
        final DAOListConfiguration cfg = new DAOListConfiguration(Restrictions.eq(weak.getOwner().getLocalProperty(), owner));
        if (weak.getEntity().getDefaultSortField() != null) {
            final Field sortField = weak.getEntity().getFieldById(weak.getEntity().getDefaultSortField(), weak.getContext());
            final ListSort sort = new ListSort(sortField, weak.getEntity().getDefaultSortDirection());
            cfg.withOrder(sort.getOrder());
        }
        final List list = weak.getDao().list(cfg);
        final PaginatedList pl = new PaginatedList();
        getContext().setEntity(entity.getEntity());
        pl.setTotal((long) list.size());
        pl.getContents().load(list, weak, weak.getEntity().getOperation("list", null));
        return pl;
    }

    @Override
    public PaginatedList getPaginatedList(ContextualEntity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize, ContextualEntity owner, String ownerId) throws PMException {
        entity.checkAuthorization();
        operation.checkAuthorization();
        final DAOListConfiguration configuration = new DAOListConfiguration();
        final PaginatedList pl = new PaginatedList();
        final Criterion search = sessionEntityData.getSearchCriteria().getCriterion();
        if (owner != null) {
            final Object ownerObject = owner.getDao().get(ownerId);
            configuration.getRestrictions().add(Restrictions.eq(entity.getOwner().getLocalProperty(), ownerObject));
        }
        if (search != null) {
            configuration.getRestrictions().add(search);
            configuration.getAliases().addAll(sessionEntityData.getSearchCriteria().getAliases());
        }
        if (sessionEntityData.getSort().isSorted()) {
            final Order order = sessionEntityData.getSort().getOrder();
            final String property = order.getPropertyName();
            if (property.contains(".")) { //need alias
                final String alias = property.substring(0, property.indexOf("."));
                configuration.withAlias(alias, alias);
            }
            configuration.withOrder(order);
        }
        final DAO dao = entity.getDao();
        if (entity.getEntity().isPaginable()) {
            pl.setPageSize(pageSize != null ? pageSize : sessionEntityData.getPageSize());
            pl.setPage(page != null ? page : sessionEntityData.getPage());
            configuration.setFrom(pl.from());
            configuration.setMax(pl.getPageSize());
            if (entity.getEntity().isCountable()) {
                pl.setTotal(dao.count(configuration));
            }
            sessionEntityData.setPage(pl.getPage());
            sessionEntityData.setPageSize(pl.getPageSize());
        }
        final List list = dao.list(configuration);
        pl.getContents().load(list, entity, operation);
        for (Field field : entity.getEntity().getOrderedFields(entity.getContext())) {
            if (field.getSearcher() != null) {
                try {
                    field.checkAuthorization();
                    pl.getFieldSearchs().put(field, field.getSearcher().visualization(field));
                } catch (NotAuthorizedException e) {
                }
            }
        }
        //getJpm().audit(); //not for now
        return pl;
    }

    @Override
    public IdentifiedObject update(Entity entity, String context, Operation operation, EntityInstance instance, Map<String, String[]> parameters) throws PMException {
        final String instanceId = instance.getIobject().getId();
        final Object object = entity.getDao(context).get(instanceId);
        instance.getIobject().setObject(object);
        try {
            processFields(entity, operation, object, instance, parameters);
            preExecute(operation, object);
            entity.getDao(context).update(object);
            postExecute(operation, object);
            getJpm().audit(entity, operation, instance.getIobject());
            return new IdentifiedObject(instanceId, object);
        } catch (PMException e) {
            entity.getDao(context).detach(object);
            throw e;
        }
    }

    @Override
    public IdentifiedObject delete(Entity entity, String context, Operation operation, String instanceId) throws PMException {
        final Object object = entity.getDao(context).get(instanceId); //current object
        preExecute(operation, object);
        entity.getDao(context).delete(object);
        postExecute(operation, object);
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        return iobject;
    }

    @Override
    public IdentifiedObject get(Entity entity, String context, Operation operation, String instanceId) throws PMException {
        preExecute(operation, null);
        final Object object = entity.getDao(context).get(instanceId); //current object
        postExecute(operation, object);
        return new IdentifiedObject(instanceId, object);
    }

    @Override
    public IdentifiedObject get(Entity entity, String context, String instanceId) throws PMException {
        return new IdentifiedObject(instanceId, entity.getDao(context).get(instanceId));
    }

    @Override
    public IdentifiedObject save(Entity entity, String context, Operation operation, EntityInstance instance, Map<String, String[]> parameters) throws PMException {
        final Object object = JPMUtils.newInstance(entity.getClazz());
        processFields(entity, operation, object, instance, parameters);
        preExecute(operation, object);
        entity.getDao(context).save(object);
        postExecute(operation, object);
        final String instanceId = entity.getDao(context).getId(object).toString();
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        return iobject;
    }

    @Override
    public IdentifiedObject save(Entity owner, String ownerId, Entity entity, String context, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        final Object ownerObject = owner.getDao(context).get(ownerId);
        final Object object = JPMUtils.newInstance(entity.getClazz());
        JPMUtils.set(object, entity.getOwner(context).getLocalProperty(), ownerObject);
        processFields(entity, operation, object, entityInstance, parameters);
        preExecute(operation, object);
        entity.getDao(context).save(object);
        postExecute(operation, object);
        final String instanceId = entity.getDao(context).getId(object).toString();
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        return iobject;
    }
}
