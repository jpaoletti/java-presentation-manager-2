package jpaoletti.jpm2.web.controller;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.PMException;
import static jpaoletti.jpm2.core.converter.ToStringConverter.DISPLAY_PATTERN;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.FieldNotFoundException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.exception.OperationNotFoundException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.ListFilter;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.model.RelatedListFilter;
import jpaoletti.jpm2.core.model.UserSearch;
import jpaoletti.jpm2.core.search.ISearcher;
import jpaoletti.jpm2.core.search.Searcher;
import jpaoletti.jpm2.core.search.Searcher.DescribedCriterion;
import jpaoletti.jpm2.core.service.MiscEntityService;
import jpaoletti.jpm2.util.JPMUtils;
import static jpaoletti.jpm2.util.XlsUtils.xlsTobytes;
import jpaoletti.jpm2.web.ObjectConverterData;
import jpaoletti.jpm2.web.ObjectConverterData.ObjectConverterDataItem;
import static jpaoletti.jpm2.web.controller.ShowController.OP_SHOW;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class ListController extends BaseController {

    public static final String PAGE1 = "page=1";
    public static final String OP_LIST = "list";

    @Autowired
    private MiscEntityService miscEntityService;

    @Autowired
    private WebApplicationContext ctx;

    @GetMapping(value = "/jpm/{entity}.json", headers = "Accept=application/json" /*, produces = {MediaType.APPLICATION_JSON_VALUE}*/)
    @ResponseBody
    public ObjectConverterData listObject(
            @PathVariable Entity entity,
            @RequestParam String textField,
            @RequestParam(required = false, defaultValue = "false") boolean useToString,
            @RequestParam(required = false) String relatedValue,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String ownerId,
            @RequestParam(required = false) String currentId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "2147483647") Integer pageSize) throws PMException {

        final Integer ps = (pageSize == null) ? 20 : pageSize;
        final ObjectConverterData r = new ObjectConverterData();
        r.setResults(new ArrayList<>());
        try {
            final DAOListConfiguration cl = new DAOListConfiguration((page - 1) * ps, ps);
            final List<Criterion> restrictions = new ArrayList<>();
            if (filter != null && !"".equals(filter)) {
                final ListFilter lfilter = (ListFilter) ctx.getBean(filter);
                synchronized (lfilter) {
                    if (lfilter instanceof RelatedListFilter) {
                        ((RelatedListFilter) lfilter).setRelatedValue(relatedValue);
                    }
                    final Criterion c = lfilter.getListFilter(cl, entity, getSessionEntityData(entity), currentId, owner, ownerId);
                    if (c != null) {
                        restrictions.add(c);
                    }
                }
            }
            if (!"".equals(query)) {
                final Map<String, String[]> searcherParameters = new LinkedHashMap();
                searcherParameters.put("value", new String[]{query});
                searcherParameters.put("operator", new String[]{"li"});
                //Field can be a conbintaion of fields like "[{code}] {description}"
                //Old style is supported if there is no { in the textField
                if (!textField.contains("{")) {
                    final Field field = entity.getFieldById(textField, getContext().getEntityContext());
                    if (field.getSearcher() == null) {
                        restrictions.add(Restrictions.ilike(field.getProperty(), query, MatchMode.ANYWHERE));
                    } else if (field.getSearcher() instanceof Searcher) {
                        try {
                            final DescribedCriterion dc = ((Searcher) field.getSearcher()).build(entity, field, searcherParameters);
                            for (DAOListConfiguration.DAOListConfigurationAlias alias : dc.getAliases()) {
                                cl.withAlias(alias.getProperty(), alias.getAlias());
                            }
                            restrictions.add(dc.getCriterion());
                        } catch (Exception e) {
                            //We ignore any errors avoiding the filter
                        }
                    } else {
                        // JPA ISearcher not supported in this context (requires Hibernate Criteria)
                        restrictions.add(Restrictions.ilike(field.getProperty(), query, MatchMode.ANYWHERE));
                    }
                } else {
                    final Disjunction disjunction = Restrictions.disjunction();
                    final Matcher matcher = DISPLAY_PATTERN.matcher(textField);
                    while (matcher.find()) {
                        final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                        //Starting with !, properties are ignored
                        if (!_display_field.startsWith("!")) {
                            final Field field2 = entity.getFieldById(_display_field, getContext().getEntityContext());
                            final String property = field2.getProperty();
                            if (property.contains(".")) {//need an alias
                                final String alias = property.substring(0, property.indexOf("."));
                                cl.withAlias(alias, alias);
                            }
                            if (field2.getSearcher() == null) {
                                disjunction.add(Restrictions.ilike(property, query, MatchMode.ANYWHERE));
                            } else if (field2.getSearcher() instanceof Searcher) {
                                try {
                                    final DescribedCriterion dc = ((Searcher) field2.getSearcher()).build(entity, field2, searcherParameters);
                                    for (DAOListConfiguration.DAOListConfigurationAlias alias : dc.getAliases()) {
                                        cl.withAlias(alias.getProperty(), alias.getAlias());
                                    }
                                    disjunction.add(dc.getCriterion());
                                } catch (Exception e) {
                                    //We ignore any errors avoiding the filter
                                }
                            } else {
                                // JPA ISearcher not supported in this context (requires Hibernate Criteria)
                                disjunction.add(Restrictions.ilike(property, query, MatchMode.ANYWHERE));
                            }
                        }
                    }
                    restrictions.add(disjunction);
                }
            }
            if (sortBy != null && !sortBy.equals("")) {
                if (sortBy.startsWith("-")) {
                    cl.withOrder(Order.desc(sortBy.substring(1)));
                } else {
                    cl.withOrder(Order.asc(sortBy));
                }
            }
            if (StringUtils.isNotEmpty(owner) && entity.getOwner() != null) {
                final ContextualEntity cowner = getJpm().getContextualEntity(owner);
                if (cowner != null) {
                    final Object ownerObject = cowner.getDao().get(ownerId);
                    restrictions.add(Restrictions.eq(entity.getOwner().getLocalProperty(), ownerObject));
                }
            }
            final List list = entity.getDao(getContext().getEntityContext()).list(cl.withRestrictions(restrictions));
            r.setMore(list.size() == ps);
            for (Object object : list) {
                getObjectDisplay(r, entity, object, useToString, textField);
            }
        } catch (FieldNotFoundException e) {
            r.getResults().add(new ObjectConverterDataItem("?", "¿¿ " + textField + " ??"));
        }
        return r;
    }

    @GetMapping(value = {"/jpm/{entity}/{operationId:" + OP_LIST + "}"})
    public ModelAndView list(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) throws PMException {
        final Entity entity = getContext().getEntity();
        if (entity.isWeak(getContext().getEntityContext()) && !entity.getOwner(getContext().getEntityContext()).isOptional()) {
            throw new NotAuthorizedException();
        }
        final ModelAndView mav = generalList(page, pageSize, null, null);
        getContext().setEntityInstance(new EntityInstance(getContext()));
        return mav;
    }

    @GetMapping(value = {"/jpm/{owner}/{ownerId}/{entity}/{operationId:" + OP_LIST + "}"})
    public ModelAndView list(
            @PathVariable String owner,
            @PathVariable String ownerId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false) Integer pageSize) throws PMException {
        final Entity entity = getContext().getEntity();
        if (!entity.isWeak(getContext().getEntityContext())) {
            throw new NotAuthorizedException();
        }
        final ContextualEntity cowner = getJpm().getContextualEntity(owner);
        final ModelAndView mav = generalList(page, pageSize, cowner, ownerId);
        final EntityInstance instance = new EntityInstance(getContext());
        instance.configureOwner(entity, getContext().getEntityContext(), cowner.getDao().get(ownerId));
        getContext().setEntityInstance(instance);
        return mav;
    }

    /**
     * List for weak entities.
     *
     * @param _entity Owner entity
     * @param instanceId Owner entity's id
     * @param _weak weak entity
     * @param showOperations
     * @return
     * @throws PMException
     */
    @GetMapping(value = {"/jpm/{entity}/{instanceId}/{weak}/weaklist"})
    public ModelAndView weaklist(
            @PathVariable(value = "entity") String _entity,
            @PathVariable String instanceId,
            @PathVariable(value = "weak") String _weak,
            @RequestParam(required = false, defaultValue = "false") boolean showOperations) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-list-weak");
        final ContextualEntity centity = getJpm().getContextualEntity(_entity);
        final ContextualEntity cweak = getJpm().getContextualEntity(_weak);
        final Entity entity = centity.getEntity();
        final Entity weak = cweak.getEntity();

        final PaginatedList weakList = getService().getWeakList(centity, instanceId, cweak);

        final Operation operation = weak.getOperation(OP_LIST); //fixme
        getContext().set(entity, entity.isContainingListOperation() ? entity.getOperation(OP_LIST, getContext().getContext()) : entity.getOperation(OP_SHOW, getContext().getContext())); //fixme
        getContext().setEntityContext(centity.getContext());
        getContext().setEntityInstance(new EntityInstance(getContext()));
        getContext().set(weak, operation);
        getContext().setEntityContext(cweak.getContext());
        mav.addObject("paginatedList", weakList);
        mav.addObject("showOperations", showOperations);
        mav.addObject("compactOperations", operation.isCompact());
        return mav;
    }

    @PostMapping(value = "/jpm/{entity}/saveCurrentSearch")//WORK IN PROGESS
    public String saveCurrentSearch(@RequestParam String field, @RequestParam String name) throws PMException {
        final UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UserSearch res = new UserSearch();
        res.setEnabled(true);
        res.setName(name);
        res.setUsername(user.getUsername());
        res.setEntity(getContext().getEntityContext());
        res.setField(field);
        res.setParameters(name);
        return buildRedirect(getContext().getEntity(), null, OP_LIST, null);
    }

    @PostMapping(value = "/jpm/{entity}/setVisibleColumns")
    public String setVisibleColumns(
            @PathVariable Entity entity,
            @RequestParam List<String> column,
            HttpServletRequest request) throws PMException {
        miscEntityService.setVisibleColumns(getAuthorizationService().getCurrentUsername(), getContext().getContextualEntity(), column);
        return buildRedirect(entity, null, OP_LIST, null);
    }

    @PostMapping(value = "/jpm/{owner}/{ownerId}/{entity}/setVisibleColumns")
    public String setVisibleColumns(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam List<String> column,
            HttpServletRequest request) throws PMException {
        miscEntityService.setVisibleColumns(getAuthorizationService().getCurrentUsername(), getContext().getContextualEntity(), column);
        return buildRedirect(owner, ownerId, entity, null, OP_LIST, null);
    }

    @PostMapping(value = "/jpm/{entity}/addSearch")
    public String addSearch(
            @PathVariable Entity entity,
            @RequestParam String fieldId,
            HttpServletRequest request) throws PMException {
        final Field field = entity.getFieldById(fieldId, getContext().getEntityContext());
        String params = null;
        if (field.getSearcher() != null) {
            final Object searcher = field.getSearcher();
            if (searcher instanceof Searcher) {
                final DescribedCriterion build = ((Searcher) searcher).build(entity, field, request.getParameterMap());
                if (build != null) {
                    getSessionEntityData(entity).getSearchCriteria().addDefinition(fieldId, build);
                    params = PAGE1;
                }
            } else if (searcher instanceof ISearcher) {
                final jpaoletti.jpm2.core.search.ISearchResult result = ((ISearcher) searcher).build(entity, field, request.getParameterMap());
                if (result != null) {
                    getSessionEntityData(entity).getSearchCriteria().addSearchResult(fieldId, result);
                    params = PAGE1;
                }
            }
        }
        return buildRedirect(entity, null, OP_LIST, params);
    }

    @PostMapping(value = "/jpm/{owner}/{ownerId}/{entity}/addSearch")
    public String addWeakSearch(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam String fieldId,
            HttpServletRequest request) throws PMException {
        final Field field = entity.getFieldById(fieldId, getContext().getEntityContext());
        String params = null;
        if (field.getSearcher() != null) {
            final Object searcher = field.getSearcher();
            if (searcher instanceof Searcher) {
                final DescribedCriterion build = ((Searcher) searcher).build(entity, field, request.getParameterMap());
                if (build != null) {
                    getSessionEntityData(entity).getSearchCriteria().addDefinition(fieldId, build);
                    params = PAGE1;
                }
            } else if (searcher instanceof ISearcher) {
                final jpaoletti.jpm2.core.search.ISearchResult result = ((ISearcher) searcher).build(entity, field, request.getParameterMap());
                if (result != null) {
                    getSessionEntityData(entity).getSearchCriteria().addSearchResult(fieldId, result);
                    params = PAGE1;
                }
            }
        }
        return buildRedirect(owner, ownerId, entity, null, OP_LIST, params);
    }

    @GetMapping(value = "/jpm/{entity}/removeSearch")
    public String removeSearch(
            @PathVariable Entity entity,
            @RequestParam Integer i) throws PMException {
        getSessionEntityData(entity).getSearchCriteria().removeDefinition(i);
        return buildRedirect(entity, null, OP_LIST, PAGE1);
    }

    @GetMapping(value = "/jpm/{owner}/{ownerId}/{entity}/removeSearch")
    public String removeWeakSearch(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam Integer i) throws PMException {
        getSessionEntityData(entity).getSearchCriteria().removeDefinition(i);
        return buildRedirect(owner, ownerId, entity, null, OP_LIST, PAGE1);
    }

    @GetMapping(value = "/jpm/{entity}/removeAllSearch")
    public String removeAllSearch(@PathVariable Entity entity) throws PMException {
        getSessionEntityData(entity).getSearchCriteria().clear();
        return buildRedirect(entity, null, OP_LIST, PAGE1);
    }

    @GetMapping(value = "/jpm/{owner}/{ownerId}/{entity}/removeAllSearch")
    public String removeAllSearchWeak(@PathVariable Entity owner, @PathVariable String ownerId, @PathVariable Entity entity) throws PMException {
        getSessionEntityData(entity).getSearchCriteria().clear();
        return buildRedirect(owner, ownerId, entity, null, OP_LIST, PAGE1);
    }

    @GetMapping(value = "/jpm/{entity}/sort")
    public String sort(@PathVariable Entity entity, @RequestParam String fieldId) throws PMException {
        getSessionEntityData(entity).getSort().set(entity.getFieldById(fieldId, getContext().getEntityContext()));
        return buildRedirect(entity, null, OP_LIST, null);
    }

    @GetMapping(value = "/jpm/{owner}/{ownerId}/{entity}/sort")
    public String sortWeak(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam String fieldId) throws PMException {
        getSessionEntityData(entity).getSort().set(entity.getFieldById(fieldId, getContext().getEntityContext()));
        return buildRedirect(owner, ownerId, entity, null, OP_LIST, null);
    }

    protected ModelAndView generalList(Integer page, Integer pageSize, ContextualEntity owner, String ownerId) throws PMException {
        final Entity entity = getContext().getEntity();
        final ModelAndView mav = new ModelAndView("jpm-" + OP_LIST);
        final PaginatedList paginatedList = getService().getPaginatedList(
                getContext().getContextualEntity(),
                getContext().getOperation(),
                getSessionEntityData(entity), page, pageSize, owner, ownerId);
        mav.addObject("paginatedList", paginatedList);
        mav.addObject("compactOperations", getContext().getOperation().isCompact());
        mav.addObject("sessionEntityData", getSessionEntityData(entity));
        final List<String> visibleColumns = miscEntityService.getVisibleColumns(getAuthorizationService().getCurrentUsername(), getContext().getContextualEntity());
        mav.addObject("visibleColumns", visibleColumns);
        return mav;
    }

    public WebApplicationContext getCtx() {
        return ctx;
    }

    public void setCtx(WebApplicationContext ctx) {
        this.ctx = ctx;
    }

    protected void getObjectDisplay(final ObjectConverterData r, Entity entity, Object object, boolean useToString, String textField) throws ConfigurationException {
        if (!textField.contains("{")) {
            final Field field = entity.getFieldById(textField, getContext().getEntityContext());
            r.getResults().add(new ObjectConverterDataItem(
                    entity.getDao(getContext().getEntityContext()).getId(object).toString(),
                    (useToString) ? object.toString() : String.valueOf(JPMUtils.get(object, field.getProperty()))));
        } else {
            final Matcher matcher = DISPLAY_PATTERN.matcher(textField);
            String finalValue = textField;
            while (matcher.find()) {
                final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                final Field field2 = entity.getFieldById(_display_field.replaceAll("\\!", ""), getContext().getEntityContext());
                finalValue = finalValue.replace("{" + _display_field + "}", String.valueOf(JPMUtils.get(object, field2.getProperty())));
            }
            r.getResults().add(new ObjectConverterDataItem(
                    entity.getDao(getContext().getEntityContext()).getId(object).toString(),
                    (useToString) ? object.toString() : finalValue));
        }
    }

    protected Operation getOperation(Entity entity) throws OperationNotFoundException, NotAuthorizedException {
        return entity.getOperation(OP_LIST); //mmmh
    }

    @GetMapping(value = {"/jpm/{owner}/{ownerId}/{entity}/{operationId:toExcel}"})
    public void toExcel(@PathVariable String owner, @PathVariable String ownerId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Entity entity = getContext().getEntity();
        if (!entity.isWeak(getContext().getEntityContext())) {
            throw new NotAuthorizedException();
        }
        final ContextualEntity cowner = getJpm().getContextualEntity(owner);
        final Workbook wb = getService().toExcel(entity, getSessionEntityData(entity), cowner, ownerId);
        response.setContentType("application/vnd.ms-excel");
        final String cleanFileName = Normalizer.normalize(StringEscapeUtils.unescapeHtml4(entity.getPluralTitle()), Normalizer.Form.NFD).replaceAll("\\p{M}", ""); // "papa"
        response.addHeader("Content-Disposition", "attachment;filename=" + cleanFileName + ".xls");
        response.getOutputStream().write(xlsTobytes(wb));
    }

    @GetMapping(value = {"/jpm/{entity}/{operationId:toExcel}"})
    public void toExcel(HttpServletResponse response) throws Exception {
        final Entity entity = getContext().getEntity();
        final Workbook wb = getService().toExcel(entity, getSessionEntityData(entity), null, null);
        response.setContentType("application/vnd.ms-excel");
        final String cleanFileName = Normalizer.normalize(StringEscapeUtils.unescapeHtml4(entity.getPluralTitle()), Normalizer.Form.NFD).replaceAll("\\p{M}", ""); // "papa"
        response.addHeader("Content-Disposition", "attachment;filename=" + cleanFileName + ".xls");
        response.getOutputStream().write(xlsTobytes(wb));
    }

}
