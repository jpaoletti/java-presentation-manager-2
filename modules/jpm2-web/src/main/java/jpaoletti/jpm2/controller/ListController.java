package jpaoletti.jpm2.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.service.JPMService;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.PaginatedList;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.ObjectConverterData;
import jpaoletti.jpm2.web.ObjectConverterData.ObjectConverterDataItem;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class ListController extends BaseController {

    @Autowired
    private JPMService service;

    @RequestMapping(value = "/jpm/{entity}", method = RequestMethod.GET, headers = "Accept=application/json" /*, produces = {MediaType.APPLICATION_JSON_VALUE}*/)
    @ResponseBody
    public ObjectConverterData listObject(
            @PathVariable Entity entity, @RequestParam String textField,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) throws PMException {
        final Field field = entity.getFieldById(textField);
        final Integer ps = (pageSize == null) ? 20 : pageSize;
        final ObjectConverterData r = new ObjectConverterData();
        final List list = entity.getDao().list((page - 1) * ps, ps, Restrictions.like(field.getProperty(), query, MatchMode.ANYWHERE));
        r.setMore(list.size() == ps);
        r.setResults(new ArrayList<ObjectConverterDataItem>());
        for (Object object : list) {
            r.getResults().add(new ObjectConverterDataItem(
                    entity.getDao().getId(object).toString(),
                    JPMUtils.get(object, field.getProperty()).toString()));
        }
        return r;
    }

    @RequestMapping(value = {"/jpm/{entity}", "/jpm/{entity}/list"}, method = RequestMethod.GET)
    public ModelAndView list(@PathVariable Entity entity,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-list");
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("list"));
        final Operation operation = getContext().getOperation();

        final PaginatedList paginatedList = getService().getPaginatedList(entity, operation, getSessionEntityData(entity), page, pageSize);

        mav.addObject("entity", entity);
        mav.addObject("operation", operation);
        mav.addObject("paginatedList", paginatedList);
        mav.addObject("generalOperations", entity.getOperationsFor(null, operation, OperationScope.GENERAL));
        mav.addObject("selectedOperations", entity.getOperationsFor(null, operation, OperationScope.SELECTED));
        mav.addObject("sessionEntityData", getSessionEntityData(entity));
        return mav;
    }

    @RequestMapping(value = "/jpm/{entity}/addSearch")
    public String addSearch(@PathVariable Entity entity, @RequestParam String fieldId, HttpServletRequest request) throws PMException {
        try {
            final Field field = entity.getFieldById(fieldId);
            if (field.getSearcher() != null) {
                final Criterion build = field.getSearcher().build(field, request.getParameterMap());
                getSessionEntityData(entity).getSearchCriteria().addDefinition(fieldId, build);
            }
        } catch (Exception e) {
        }
        return "redirect:/jpm/" + entity.getId() + "/";
    }

    @RequestMapping(value = "/jpm/{entity}/removeSearch")
    public String removeSearch(@PathVariable Entity entity, @RequestParam Integer i) throws PMException {
        try {
            getSessionEntityData(entity).getSearchCriteria().removeDefinition(i);
        } catch (Exception e) {
        }
        return "redirect:/jpm/" + entity.getId() + "/";
    }

    protected Criterion getSearch(Entity entity) {
        return getSessionEntityData(entity).getSearchCriteria().getCriterion();
    }

    public JPMService getService() {
        return service;
    }

    public void setService(JPMService service) {
        this.service = service;
    }
}