package jpaoletti.jpm2.core.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.criteria.JoinType;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DAO;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.dao.DAOOrder;
import jpaoletti.jpm2.core.dao.IDAOListConfiguration;
import jpaoletti.jpm2.core.dao.JPADAOListConfiguration;
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
import jpaoletti.jpm2.core.search.ISearcher;
import jpaoletti.jpm2.core.search.Searcher;
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

    private static final org.apache.logging.log4j.Logger LOG = JPMUtils.getLogger(JPMUtils.SERVICE);

    @Override
    public PaginatedList getWeakList(ContextualEntity entity, String instanceId, ContextualEntity weak, boolean paginated, Integer page, Integer pageSize) throws PMException {
        LOG.debug("getWeakList IN owner={} ownerId={} weak={} paginated={} page={} pageSize={}",
                entity, instanceId, weak, paginated, page, pageSize);
        final Object owner = entity.getDao().get(instanceId);
        final IDAOListConfiguration cfg = weak.getDao().build();
        final Entity weakEntity = weak.getEntity();
        addOwnerRestriction(weak, cfg, entity, owner);
        if (weakEntity.getDefaultSortField(entity.getContext()) != null) {
            final Field sortField = weakEntity.getFieldById(weakEntity.getDefaultSortField(entity.getContext()), weak.getContext());
            final ListSort sort = new ListSort(sortField, weakEntity.getDefaultSortDirection(entity.getContext()));
            addOrder(cfg, sort);
        }
        final PaginatedList pl = new PaginatedList();
        getContext().setEntity(entity.getEntity());
        final boolean applyPagination = paginated && weakEntity.isPaginable();
        if (applyPagination) {
            pl.setPageSize(pageSize != null ? pageSize : weakEntity.getPageSize());
            pl.setPage(page != null ? page : 1);
            cfg.setFrom(pl.from());
            cfg.setMax(pl.getPageSize());
            if (weakEntity.isCountable()) {
                pl.setTotal(weak.getDao().count(cfg));
            }
        }
        final List list = weak.getDao().list(cfg);
        if (!applyPagination) {
            pl.setTotal((long) list.size());
        }
        pl.getContents().load(list, weak, weak.getEntity().getOperation("list"));
        LOG.debug("getWeakList OUT weak={} total={} listSize={}", weak, pl.getTotal(), list.size());
        return pl;
    }

    @Override
    public PaginatedList getPaginatedList(ContextualEntity entity, Operation operation, SessionEntityData sessionEntityData, Integer page, Integer pageSize, ContextualEntity owner, String ownerId) throws PMException {
        LOG.debug("getPaginatedList IN entity={} op={} page={} pageSize={} owner={} ownerId={}",
                entity, operation, page, pageSize, owner, ownerId);
        getContext().setOwnerId(ownerId);
        entity.checkAuthorization();
        if (operation != null) {
            operation.checkAuthorization();
        }
        final IDAOListConfiguration configuration = entity.getDao().build();
        final PaginatedList pl = new PaginatedList();
        final Criterion search = sessionEntityData.getSearchCriteria().getCriterion();
        if (owner != null) {
            final Object ownerObject = owner.getDao().get(ownerId);
            //configuration.getRestrictions().add(Restrictions.eq(entity.getOwner().getLocalProperty(), ownerObject));
            addOwnerRestriction(entity, configuration, owner, ownerObject);
        }
        // Apply search criteria
        if (sessionEntityData.getSearchCriteria().hasSearchResults()) {
            // NEW: Use generic search results (works with both Hibernate and JPA)
            sessionEntityData.getSearchCriteria().applyTo(configuration);
        } else if (search != null) {
            // LEGACY: Fallback to old Hibernate Criterion method
            addSearchCriteria(configuration, search, sessionEntityData.getSearchCriteria().getAliases());
        }
        if (sessionEntityData.getSort().isSorted()) {
            addSort(configuration, sessionEntityData.getSort());
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
                    final Object searcher = field.getSearcher();
                    String visualization = null;
                    if (searcher instanceof Searcher) {
                        visualization = ((Searcher) searcher).visualization(entity.getEntity(), field);
                    } else if (searcher instanceof ISearcher) {
                        visualization = ((ISearcher) searcher).visualization(entity.getEntity(), field);
                    }
                    if (visualization != null) {
                        pl.getFieldSearchs().put(field, visualization);
                    }
                } catch (NotAuthorizedException e) {
                }
            }
        }
        //getJpm().audit(); //not for now
        LOG.debug("getPaginatedList OUT entity={} total={} listSize={}", entity, pl.getTotal(), list.size());
        return pl;
    }

    @Override
    public IdentifiedObject update(Entity entity, String context, Operation operation, EntityInstance instance, Map<String, String[]> parameters) throws PMException {
        final String instanceId = instance.getIobject().getId();
        if (LOG.isDebugEnabled()) {
            LOG.debug("update IN entity={} ctx={} op={} instanceId={} params={}",
                    entity.getId(), context, operation, instanceId, JPMUtils.formatParams(parameters));
        }
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
            final String newId = String.valueOf(entity.getDao(context).getId(object));
            instance.getIobject().setId(newId);
            postExecute(operation, object);
            getJpm().audit(entity, operation, instance.getIobject(), originalValues);
            LOG.debug("update OUT entity={} newId={} class={}", entity.getId(), newId, object.getClass().getSimpleName());
            return new IdentifiedObject(newId, object);
        } catch (PMException e) {
            LOG.debug("update FAIL entity={} instanceId={} error={}", entity.getId(), instanceId, e.getMessage());
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
    public byte[] toPdf(Entity entity, SessionEntityData sed, ContextualEntity owner, String ownerId,
            List<String> fieldOrder, boolean landscape, String pageSize, String title) throws PMException {
        final Integer page = sed.getPage();
        final Integer pageSize2 = sed.getPageSize();
        final PaginatedList paginatedList = getJpm().getService().getPaginatedList(getContext().getContextualEntity(), getContext().getOperation(), sed, 1, Integer.MAX_VALUE, owner, ownerId);
        sed.setPageSize(pageSize2);
        sed.setPage(page);
        final EntityInstanceList list = paginatedList.getContents();
        if (list == null || list.isEmpty()) {
            throw new PMException("jpm.toPdf.noData");
        }
        // Resolve the columns to render, their headers and their alignments.
        final List<Field> columns = resolvePdfColumns(paginatedList.getFields(), fieldOrder);
        final List<String> headers = new ArrayList<>();
        final List<String> alignments = new ArrayList<>();
        for (Field field : columns) {
            headers.add(replaceHtmlCodeAccents(field.getTitle(entity)));
            alignments.add(field.getAlign());
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(LocaleContextHolder.getLocale());
        numberFormat.setGroupingUsed(true);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(4);
        final List<List<String>> rows = new ArrayList<>();
        for (EntityInstance entityInstance : list) {
            final List<String> row = new ArrayList<>();
            for (Field field : columns) {
                final Object value = entityInstance.getValues().get(field.getId());
                if (value == null) {
                    row.add("");
                } else if (value instanceof Date) {
                    row.add(dateFormat.format((Date) value));
                } else if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
                    row.add(numberFormat.format(value));
                } else {
                    row.add(jpaoletti.jpm2.util.PdfUtils.stripHtml(value.toString()));
                }
            }
            rows.add(row);
        }
        final String documentTitle = (title == null || title.trim().isEmpty()) ? entity.getPluralTitle() : title;
        try {
            return jpaoletti.jpm2.util.PdfUtils.foToPdf(
                    jpaoletti.jpm2.util.PdfUtils.buildFo(documentTitle, headers, rows, alignments, landscape, pageSize));
        } catch (Exception e) {
            LOG.error("toPdf FAIL entity={} error={}", entity.getId(), e.getMessage(), e);
            throw new PMException("jpm.toPdf.error");
        }
    }

    /**
     * Resolve which fields become PDF columns. When an explicit order is given
     * those field ids are used (intersected with the visible/exportable fields
     * and in that order). Otherwise the default is the exportable fields that
     * have an explicit "toPdf" config, falling back to all exportable fields
     * when none was marked.
     */
    private List<Field> resolvePdfColumns(List<Field> visibleFields, List<String> fieldOrder) {
        if (fieldOrder != null && !fieldOrder.isEmpty()) {
            final Map<String, Field> byId = new LinkedHashMap<>();
            for (Field field : visibleFields) {
                byId.put(field.getId(), field);
            }
            final List<Field> ordered = new ArrayList<>();
            for (String id : fieldOrder) {
                final Field field = byId.get(id.trim());
                if (field != null) {
                    ordered.add(field);
                }
            }
            if (!ordered.isEmpty()) {
                return ordered;
            }
        }
        final List<Field> marked = new ArrayList<>();
        for (Field field : visibleFields) {
            if (field.hasConfigFor("toPdf")) {
                marked.add(field);
            }
        }
        return marked.isEmpty() ? visibleFields : marked;
    }

    @Override
    public IdentifiedObject delete(Entity entity, String context, Operation operation, String instanceId) throws PMException {
        LOG.debug("delete IN entity={} ctx={} op={} instanceId={}", entity.getId(), context, operation, instanceId);
        final Object object = entity.getDao(context).get(instanceId); //current object
        preExecute(operation, object);
        entity.getDao(context).delete(object);
        postExecute(operation, object);
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        LOG.debug("delete OUT entity={} instanceId={}", entity.getId(), instanceId);
        return iobject;
    }

    @Override
    public IdentifiedObject get(Entity entity, String context, Operation operation, String instanceId) throws PMException {
        //preExecute(operation, null);
        LOG.debug("get IN entity={} ctx={} op={} instanceId={}", entity.getId(), context, operation, instanceId);
        final Object object = entity.getDao(context).get(instanceId); //current object
        //postExecute(operation, object);
        LOG.debug("get OUT entity={} instanceId={} found={}", entity.getId(), instanceId, object != null);
        return new IdentifiedObject(instanceId, object);
    }

    @Override
    public IdentifiedObject get(Entity entity, String context, String instanceId) throws PMException {
        return new IdentifiedObject(instanceId, entity.getDao(context).get(instanceId));
    }

    @Override
    public IdentifiedObject save(Entity entity, String context, Operation operation, EntityInstance instance, Map<String, String[]> parameters) throws PMException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("save IN entity={} ctx={} op={} params={}",
                    entity.getId(), context, operation, JPMUtils.formatParams(parameters));
        }
        final Object object = JPMUtils.newInstance(entity.getClazz());
        processFields(entity, operation, object, instance, parameters);
        preExecute(operation, object);
        entity.getDao(context).save(object);
        postExecute(operation, object);
        final String instanceId = entity.getDao(context).getId(object).toString();
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        LOG.debug("save OUT entity={} newId={} class={}", entity.getId(), instanceId, object.getClass().getSimpleName());
        return iobject;
    }

    @Override
    public IdentifiedObject save(Entity owner, String ownerId, Entity entity, String context, Operation operation, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("save(weak) IN owner={} ownerId={} entity={} ctx={} op={} params={}",
                    owner.getId(), ownerId, entity.getId(), context, operation, JPMUtils.formatParams(parameters));
        }
        final Object ownerObject = owner.getDao(context).get(ownerId);
        final Object object = JPMUtils.newInstance(entity.getClazz());
        entity.getOwner(context).setOwnerObject(getContext().getEntityContext(), object, ownerObject);
        entityInstance.setIobject(new IdentifiedObject(null, object), getContext());
        processFields(entity, operation, object, entityInstance, parameters);
        preExecute(operation, object);
        entity.getDao(context).save(object);
        postExecute(operation, object);
        final String instanceId = entity.getDao(context).getId(object).toString();
        final IdentifiedObject iobject = new IdentifiedObject(instanceId, object);
        getJpm().audit(entity, operation, iobject);
        LOG.debug("save(weak) OUT entity={} newId={} owner={} ownerId={}", entity.getId(), instanceId, owner.getId(), ownerId);
        return iobject;
    }

    /**
     * Adds an order to the configuration. Detects the configuration type and
     * applies the appropriate method.
     *
     * @param cfg the configuration (DAOListConfiguration or
     * JPADAOListConfiguration)
     * @param sort the ListSort to add
     */
    private void addOrder(IDAOListConfiguration cfg, ListSort sort) {
        if (cfg instanceof DAOListConfiguration) {
            ((DAOListConfiguration) cfg).withOrder(sort.getOrder());
        } else if (cfg instanceof JPADAOListConfiguration) {
            final Order hibernateOrder = sort.getOrder();
            final String property = hibernateOrder.getPropertyName();
            final boolean asc = sort.isAsc();
            ((JPADAOListConfiguration) cfg).withOrder(new DAOOrder(property, asc));
        }
    }

    /**
     * Adds search criteria to the configuration. For Hibernate-based
     * configurations, adds Criterion and aliases directly. For JPA-based
     * configurations, this method should only be called with Hibernate DAOs
     * (search criteria is Hibernate-specific).
     *
     * @param cfg the configuration
     * @param criterion the Hibernate Criterion to add
     * @param aliases the aliases to add
     */
    private void addSearchCriteria(IDAOListConfiguration cfg, Criterion criterion, Set<DAOListConfiguration.DAOListConfigurationAlias> aliases) {
        if (cfg instanceof DAOListConfiguration) {
            DAOListConfiguration dalCfg = (DAOListConfiguration) cfg;
            dalCfg.getRestrictions().add(criterion);
            dalCfg.getAliases().addAll(aliases);
        }
        // Note: JPA configurations don't support Hibernate Criterion directly.
        // If using JPA DAOs, search criteria needs to be converted to predicates at the Entity/Field level.
    }

    /**
     * Adds sort configuration. Detects the configuration type and applies the
     * appropriate method.
     *
     * @param cfg the configuration
     * @param sort the ListSort to add
     */
    private void addSort(IDAOListConfiguration cfg, ListSort sort) {
        if (cfg instanceof DAOListConfiguration) {
            DAOListConfiguration dalCfg = (DAOListConfiguration) cfg;
            final Order order = sort.getOrder();
            final String property = order.getPropertyName();
            if (property.contains(".")) { //need alias
                final String alias = property.substring(0, property.indexOf("."));
                dalCfg.withAlias(alias, alias);
            }
            dalCfg.withOrder(order);
        } else if (cfg instanceof JPADAOListConfiguration) {
            JPADAOListConfiguration jpaoCfg = (JPADAOListConfiguration) cfg;
            final Order hibernateOrder = sort.getOrder();
            final String property = hibernateOrder.getPropertyName();
            // El ORDER BY navega la propiedad anidada con get() encadenado (en
            // JPADAO.buildQuery), que ya crea el join implícito -> no agregar alias
            // (evitaba join duplicado / SQL malformado "order by . asc").
            final boolean asc = sort.isAsc();
            jpaoCfg.withOrder(new DAOOrder(property, asc));
        }
    }

    /**
     * Adds owner restriction to the configuration. Detects the configuration
     * type and applies the appropriate method.
     *
     * @param weak the weak entity
     * @param cfg the configuration (DAOListConfiguration or
     * JPADAOListConfiguration)
     * @param ownerEntity the owner entity
     * @param owner the owner object
     */
    private void addOwnerRestriction(ContextualEntity weak, final IDAOListConfiguration cfg, ContextualEntity ownerEntity, final Object owner) {
        if (weak != null && weak.getOwner() != null) {
            final String localProperty = weak.getOwner().getLocalProperty();
            final Object value = weak.getOwner().isOnlyId() ? ownerEntity.getDao().getId(owner) : owner;

            if (cfg instanceof DAOListConfiguration) {
                ((DAOListConfiguration) cfg).getRestrictions().add(Restrictions.eq(localProperty, value));
            } else if (cfg instanceof JPADAOListConfiguration) {
                ((JPADAOListConfiguration) cfg).withPredicate((cb, root) -> cb.equal(root.get(localProperty), value));
            }
        }
    }
}
