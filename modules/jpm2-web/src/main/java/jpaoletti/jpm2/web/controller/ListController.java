package jpaoletti.jpm2.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.NotAuthorizedException;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.ListFilter;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.core.search.Searcher.DescribedCriterion;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.ObjectConverterData;
import jpaoletti.jpm2.web.ObjectConverterData.ObjectConverterDataItem;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    public static final Pattern DISPLAY_PATTERN = Pattern.compile("\\{.*?\\}");
    public static final String OP_LIST = "list";
    @Autowired
    private WebApplicationContext ctx;

    @RequestMapping(value = "/jpm/{entity}.json", method = RequestMethod.GET, headers = "Accept=application/json" /*, produces = {MediaType.APPLICATION_JSON_VALUE}*/)
    @ResponseBody
    public ObjectConverterData listObject(
            @PathVariable Entity entity,
            @RequestParam String textField,
            @RequestParam(required = false, defaultValue = "false") boolean useToString,
            @RequestParam(required = false) String ownerId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) throws PMException {

        final Integer ps = (pageSize == null) ? 20 : pageSize;
        final ObjectConverterData r = new ObjectConverterData();
        final List<Criterion> restrictions = new ArrayList<>();
        if (filter != null && !"".equals(filter)) {
            final ListFilter lfilter = (ListFilter) ctx.getBean(filter);
            final Criterion c = lfilter.getListFilter(entity, getSessionEntityData(entity), ownerId);
            if (c != null) {
                restrictions.add(c);
            }
        }
        //Field can be a conbintaion of fields like "[{code}] {description}"
        //Old style is supported if there is no @ in the textField
        if (!textField.contains("{")) {
            final Field field = entity.getFieldById(textField);
            restrictions.add(Restrictions.like(field.getProperty(), query, MatchMode.ANYWHERE));
        } else {
            final Disjunction disjunction = Restrictions.disjunction();
            final Matcher matcher = DISPLAY_PATTERN.matcher(textField);
            while (matcher.find()) {
                final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                final Field field2 = entity.getFieldById(_display_field);
                disjunction.add(Restrictions.like(field2.getProperty(), query, MatchMode.ANYWHERE));
            }
            restrictions.add(disjunction);
        }
        final List list = entity.getDao().list((page - 1) * ps, ps, restrictions.toArray(new Criterion[restrictions.size()]));
        r.setMore(list.size() == ps);
        r.setResults(new ArrayList<ObjectConverterDataItem>());
        for (Object object : list) {
            getObjectDisplay(r, entity, object, useToString, textField);
        }
        return r;
    }

    @RequestMapping(value = {"/jpm/{entity}", "/jpm/{entity}/list"}, method = RequestMethod.GET)
    public ModelAndView list(
            @PathVariable Entity entity,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) throws PMException {
        if (entity.isWeak()) {
            throw new NotAuthorizedException();
        }
        ModelAndView mav = generalList(entity, page, pageSize, null);
        return mav;
    }

    @RequestMapping(value = {"/jpm/{owner}/{ownerId}/{entity}/list", "/jpm/{owner}/{ownerId}/{entity}"}, method = RequestMethod.GET)
    public ModelAndView list(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) throws PMException {
        if (!entity.isWeak()) {
            throw new NotAuthorizedException();
        }
        final ModelAndView mav = generalList(entity, page, pageSize, ownerId);
        mav.addObject("owner", owner);
        mav.addObject("ownerId", ownerId);
        return mav;
    }

    /**
     * List for weak entities.
     *
     * @param entity Owner entity
     * @param instanceId Owner entity's id
     * @param weak weak entity
     * @param field Owner field
     * @return
     * @throws PMException
     */
    @RequestMapping(value = {"/jpm/{entity}/{instanceId}/{weak}/weaklist"}, method = RequestMethod.GET)
    public ModelAndView weaklist(
            @PathVariable Entity entity,
            @PathVariable String instanceId,
            @PathVariable Entity weak) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-list-weak");
        final PaginatedList weakList = getService().getWeakList(entity, instanceId, weak);
        final Operation operation = weak.getOperation(OP_LIST);
        getContext().set(weak, operation);
        mav.addObject("paginatedList", weakList);
        return mav;
    }

    @RequestMapping(value = "/jpm/{entity}/addSearch")
    public String addSearch(
            @PathVariable Entity entity,
            @RequestParam String fieldId,
            HttpServletRequest request) throws PMException {
        final Field field = entity.getFieldById(fieldId);
        if (field.getSearcher() != null) {
            final DescribedCriterion build = field.getSearcher().build(field, request.getParameterMap());
            getSessionEntityData(entity).getSearchCriteria().addDefinition(fieldId, build);
        }
        return "redirect:/jpm/" + entity.getId() + "/";
    }

    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/addSearch")
    public String addWeakSearch(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam String fieldId,
            HttpServletRequest request) throws PMException {
        final Field field = entity.getFieldById(fieldId);
        if (field.getSearcher() != null) {
            final DescribedCriterion build = field.getSearcher().build(field, request.getParameterMap());
            getSessionEntityData(entity).getSearchCriteria().addDefinition(fieldId, build);
        }
        return "redirect:/jpm/" + owner.getId() + "/" + ownerId + "/" + entity.getId() + "/";
    }

    @RequestMapping(value = "/jpm/{entity}/removeSearch")
    public String removeSearch(
            @PathVariable Entity entity,
            @RequestParam Integer i) throws PMException {
        getSessionEntityData(entity).getSearchCriteria().removeDefinition(i);
        return "redirect:/jpm/" + entity.getId() + "/";
    }

    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/removeSearch")
    public String removeWeakSearch(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam Integer i) throws PMException {
        getSessionEntityData(entity).getSearchCriteria().removeDefinition(i);
        return "redirect:/jpm/" + owner.getId() + "/" + ownerId + "/" + entity.getId() + "/";
    }

    @RequestMapping(value = "/jpm/{entity}/sort")
    public String sort(@PathVariable Entity entity, @RequestParam String fieldId) throws PMException {
        getSessionEntityData(entity).getSort().set(entity.getFieldById(fieldId));
        return "redirect:/jpm/" + entity.getId() + "/";
    }

    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/sort")
    public String sortWeak(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam String fieldId) throws PMException {
        getSessionEntityData(entity).getSort().set(entity.getFieldById(fieldId));
        return "redirect:/jpm/" + owner.getId() + "/" + ownerId + "/" + entity.getId() + "/";
    }

    protected Criterion getSearch(Entity entity) throws PMException {
        return getSessionEntityData(entity).getSearchCriteria().getCriterion();
    }

    protected ModelAndView generalList(Entity entity, Integer page, Integer pageSize, String ownerId) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-" + OP_LIST);
        final Operation operation = entity.getOperation(OP_LIST);
        getContext().set(entity, operation);
        final PaginatedList paginatedList = getService().getPaginatedList(entity, operation, getSessionEntityData(entity), page, pageSize, ownerId);
        mav.addObject("paginatedList", paginatedList);
        mav.addObject("generalOperations", entity.getOperationsFor(null, operation, OperationScope.GENERAL));
        mav.addObject("selectedOperations", entity.getOperationsFor(null, operation, OperationScope.SELECTED));
        mav.addObject("sessionEntityData", getSessionEntityData(entity));
        return mav;
    }

    public WebApplicationContext getCtx() {
        return ctx;
    }

    public void setCtx(WebApplicationContext ctx) {
        this.ctx = ctx;
    }

    protected void getObjectDisplay(final ObjectConverterData r, Entity entity, Object object, boolean useToString, String textField) {
        if (!textField.contains("{")) {
            final Field field = entity.getFieldById(textField);
            r.getResults().add(new ObjectConverterDataItem(
                    entity.getDao().getId(object).toString(),
                    (useToString) ? object.toString() : JPMUtils.get(object, field.getProperty()).toString()));
        } else {
            final Matcher matcher = ListController.DISPLAY_PATTERN.matcher(textField);
            String finalValue = textField;
            while (matcher.find()) {
                final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                final Field field2 = entity.getFieldById(_display_field);
                finalValue = finalValue.replace("{" + _display_field + "}", String.valueOf(JPMUtils.get(object, field2.getProperty())));
            }
            r.getResults().add(new ObjectConverterDataItem(
                    entity.getDao().getId(object).toString(),
                    (useToString) ? object.toString() : finalValue));
        }
    }
}