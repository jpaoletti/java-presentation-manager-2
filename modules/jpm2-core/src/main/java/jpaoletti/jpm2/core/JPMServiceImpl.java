package jpaoletti.jpm2.core;

import java.util.Map;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.SessionEntityData;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.criterion.Criterion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jpaoletti
 */
@Service
public class JPMServiceImpl extends JPMServiceBase implements JPMService {

    @Override
    @Transactional(readOnly = true)
    public PaginatedList getPaginatedList(Entity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize) throws PMException {
        entity.checkAuthorization();
        operation.checkAuthorization();
        final PaginatedList pl = new PaginatedList();
        final Criterion search = sessionEntityData.getSearchCriteria().getCriterion();
        if (entity.isPaginable()) {
            pl.setPageSize(pageSize != null ? pageSize : sessionEntityData.getPageSize());
            pl.setPage(page != null ? page : sessionEntityData.getPage());
            if (search == null) {
                pl.getContents().load(entity.getDao().list(pl.from(), pl.getPageSize()), entity, operation);
                pl.setTotal(entity.getDao().count());
            } else {
                pl.getContents().load(entity.getDao().list(pl.from(), pl.getPageSize(), search), entity, operation);
                pl.setTotal(entity.getDao().count(search));
            }
            sessionEntityData.setPage(pl.getPage());
            sessionEntityData.setPageSize(pl.getPageSize());
        } else {
            if (search == null) {
                pl.getContents().load(entity.getDao().list(), entity, operation);
            } else {
                pl.getContents().load(entity.getDao().list(search), entity, operation);
            }
        }
        for (Field field : entity.getAllFields()) {
            if (field.getSearcher() != null) {
                pl.getFieldSearchs().put(field, field.getSearcher().visualization());
            }
        }
        return pl;
    }

    @Transactional(readOnly = false)
    @Override
    public Object update(Entity entity, Operation operation, String instanceId, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        final Object object = entity.getDao().get(instanceId); //current object
        try {
            processFields(entity, operation, object, entityInstance, parameters);
            preExecute(operation, object);
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                //getEntityMessages().add(e.getMsg());
            }
        }
        entity.getDao().update(object);
        postExecute(operation);
        return object;
    }

    @Override
    public void delete(Entity entity, Operation operation, String instanceId) throws PMException {
        final Object object = entity.getDao().get(instanceId); //current object
        preExecute(operation, object);
        entity.getDao().delete(object);
        postExecute(operation);
    }

    @Override
    @Transactional
    public String save(Entity entity, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        final Object object = JPMUtils.newInstance(entity.getClazz());
        try {
            processFields(entity, operation, object, entityInstance, parameters);
            preExecute(operation, object);
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                //getEntityMessages().add(e.getMsg());
            }
        }
        entity.getDao().update(object);
        postExecute(operation);
        return entity.getDao().getId(object).toString();
    }
}
