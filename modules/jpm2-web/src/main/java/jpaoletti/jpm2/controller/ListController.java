package jpaoletti.jpm2.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstanceList;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.PaginatedList;
import org.hibernate.criterion.Criterion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class ListController extends BaseController {

    @RequestMapping(value = "/jpm/{entity}", method = RequestMethod.GET)
    public ModelAndView list(@PathVariable Entity entity, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-list");
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("list"));
        final Operation operation = getContext().getOperation();
        mav.addObject("entity", entity);
        mav.addObject("operation", operation);
        final EntityInstanceList list = new EntityInstanceList();
        final PaginatedList paginatedList = new PaginatedList();
        final boolean paginable = operation.getConfig("paginable", "true").equalsIgnoreCase("true");
        if (paginable) {
            paginatedList.setPageSize(pageSize != null ? pageSize : getSessionEntityData(entity).getPageSize());
            paginatedList.setPage(page != null ? page : getSessionEntityData(entity).getPage());
            if (getSearch(entity) == null) {
                list.load(entity.getDao().list(paginatedList.from(), paginatedList.getPageSize()), entity, operation);
                paginatedList.setTotal(entity.getDao().count());
            } else {
                list.load(entity.getDao().list(paginatedList.from(), paginatedList.getPageSize(), getSearch(entity)), entity, operation);
                paginatedList.setTotal(entity.getDao().count(getSearch(entity)));
            }
            paginatedList.setContents(list);
            getSessionEntityData(entity).setPage(paginatedList.getPage());
            getSessionEntityData(entity).setPageSize(paginatedList.getPageSize());
        } else {
            if (getSearch(entity) == null) {
                list.load(entity.getDao().list(), entity, operation);
            } else {
                list.load(entity.getDao().list(getSearch(entity)), entity, operation);
            }
        }

        mav.addObject("paginatedList", paginatedList);

        final Map<Field, String> fieldSearchs = new HashMap<>();
        for (Field field : entity.getAllFields()) {
            if (field.getSearcher() != null) {
                fieldSearchs.put(field, field.getSearcher().visualization());
            }
        }
        mav.addObject("fieldSearchs", fieldSearchs);
        mav.addObject("generalOperations", entity.getOperationsFor(null, operation, OperationScope.GENERAL));
        mav.addObject("selectedOperations", entity.getOperationsFor(null, operation, OperationScope.SELECTED));
        mav.addObject("sessionEntityData", getSessionEntityData(entity));
        mav.addObject("paginable", paginable);
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
}