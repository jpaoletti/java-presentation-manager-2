package jpaoletti.jpm2.core.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.dao.DAO;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.EntityInstanceList;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.ListSort;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.SessionEntityData;
import jpaoletti.jpm2.util.JPMUtils;
import static jpaoletti.jpm2.util.XlsUtils.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.i18n.LocaleContextHolder;
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
        final DAOListConfiguration cfg = new DAOListConfiguration();
        addOwnerRestriction(weak, cfg, entity, owner);
        if (weak.getEntity().getDefaultSortField(entity.getContext()) != null) {
            final Field sortField = weak.getEntity().getFieldById(weak.getEntity().getDefaultSortField(entity.getContext()), weak.getContext());
            final ListSort sort = new ListSort(sortField, weak.getEntity().getDefaultSortDirection(entity.getContext()));
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
        getContext().setOwnerId(ownerId);
        entity.checkAuthorization();
        if (operation != null) {
            operation.checkAuthorization();
        }
        final DAOListConfiguration configuration = new DAOListConfiguration();
        final PaginatedList pl = new PaginatedList();
        final Criterion search = sessionEntityData.getSearchCriteria().getCriterion();
        if (owner != null) {
            final Object ownerObject = owner.getDao().get(ownerId);
            //configuration.getRestrictions().add(Restrictions.eq(entity.getOwner().getLocalProperty(), ownerObject));
            addOwnerRestriction(entity, configuration, owner, ownerObject);
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
                    pl.getFieldSearchs().put(field, field.getSearcher().visualization(entity.getEntity(), field));
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
        Map<String, Object> originalValues = null;
        instance.getIobject().setObject(object);
        try {
            if (entity.isDetailedAudit()) {
                originalValues = JPMUtils.getOriginalValues(entity, object);
            }
            processFields(entity, operation, object, instance, parameters);
            preExecute(operation, object);
            entity.getDao(context).update(object);
            postExecute(operation, object);
            if (originalValues != null) {
                final StringBuilder sb = new StringBuilder();
                for (Field field : entity.getFields()) {
                    final Object newValue = Converter.getValue(object, field);
                    final Object original = originalValues.get(field.getId());
                    if (!Objects.equals(newValue, original)) {
                        sb.append(String.format("<b>%s</b>: %s -&gt; %s",
                                field.getTitle(entity),
                                Objects.toString(original),
                                Objects.toString(newValue))
                        ).append("<br/>");
                    }
                }
                getJpm().audit(entity, operation, instance.getIobject(), sb.toString());
            } else {
                getJpm().audit(entity, operation, instance.getIobject());
            }
            return new IdentifiedObject(instanceId, object);
        } catch (PMException e) {
            entity.getDao(context).detach(object);
            throw e;
        }
    }

    @Override
    public Workbook toExcel(Entity entity, SessionEntityData sed, ContextualEntity owner, String ownerId) throws PMException {
        final Workbook wb = new HSSFWorkbook();
        final Integer page = sed.getPage();
        final Integer pageSize = sed.getPageSize();
        final PaginatedList paginatedList = getJpm().getService().getPaginatedList(getContext().getContextualEntity(), getContext().getOperation(), sed, 1, Integer.MAX_VALUE, owner, ownerId);
        sed.setPageSize(pageSize);
        sed.setPage(page);
        final EntityInstanceList list = paginatedList.getContents();
        if (list == null || list.isEmpty()) {
            throw new PMException("jpm.toExcel.noData");
        }
        if (list.size() > 65530) {
            throw new PMException("jpm.toExcel.tooMuchData");
        }
        final Sheet sheet = xlsNewPage(wb, new XlsFormatTitle(
                getMessage("jpm.toExcel.pageName", null, LocaleContextHolder.getLocale()),
                getMessage("jpm.toExcel.pageTitle", entity.getPluralTitle().toUpperCase(), LocaleContextHolder.getLocale())
        ));
        sheet.createFreezePane(0, 3);
        final CellStyle bold = xlsBoldStyle(wb);
        final CellStyle xlsDateStyle = xlsDateStyle(wb);
        final CellStyle xlsAmountStyle = xlsAmountStyle(wb);
        final Row headerRow = sheet.createRow(2);
        int i = 0;
        for (Field field : paginatedList.getFields()) {
            final Cell cell = headerRow.createCell(i++);
            cell.setCellStyle(bold);
            cell.setCellValue(replaceHtmlCodeAccents(field.getTitle(entity)));
        }
        int r = 3;
        for (EntityInstance entityInstance : list) {
            final Row row = sheet.createRow(r++);
            i = 0;
            for (Map.Entry<String, Object> v : entityInstance.getValues().entrySet()) {
                final Object convertedValue = v.getValue();
                if (convertedValue == null) {
                    row.createCell(i++).setCellValue("");
                } else if (convertedValue instanceof String) {
                    row.createCell(i++).setCellValue((String) convertedValue);
                } else if (convertedValue instanceof Date) {
                    xlsCellWithStyle(row.createCell(i++), xlsDateStyle).setCellValue((Date) convertedValue);
                } else if (convertedValue instanceof Boolean) {
                    row.createCell(i++).setCellValue((Boolean) convertedValue);
                } else if (convertedValue instanceof BigDecimal) {
                    xlsCellWithStyle(row.createCell(i++), xlsAmountStyle).setCellValue(((BigDecimal) convertedValue).doubleValue());
                } else {
                    row.createCell(i++).setCellValue(convertedValue.toString());
                }
            }
        }
        return xlsStrechColumns(wb);
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
        //preExecute(operation, null);
        final Object object = entity.getDao(context).get(instanceId); //current object
        //postExecute(operation, object);
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
        entity.getOwner(context).setOwnerObject(getContext().getEntityContext(), object, ownerObject);
        processFields(entity, operation, object, entityInstance, parameters);
        preExecute(operation, object);
        entity.getDao(context).save(object);
        postExecute(operation, object);
        final String instanceId = entity.getDao(context).getId(object).toString();
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        return iobject;
    }

    private void addOwnerRestriction(ContextualEntity weak, final DAOListConfiguration cfg, ContextualEntity ownerEntity, final Object owner) {
        if (weak != null && weak.getOwner() != null) {
            if (weak.getOwner().isOnlyId()) {
                cfg.getRestrictions().add(Restrictions.eq(weak.getOwner().getLocalProperty(), ownerEntity.getDao().getId(owner)));
            } else {
                cfg.getRestrictions().add(Restrictions.eq(weak.getOwner().getLocalProperty(), owner));
            }
        }
    }
}
