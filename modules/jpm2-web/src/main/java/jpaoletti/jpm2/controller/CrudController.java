package jpaoletti.jpm2.controller;

import jpaoletti.jpm2.core.JPMService;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.ObjectConverterData;
import jpaoletti.jpm2.web.ObjectConverterData.ObjectConverterDataItem;
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
public final class CrudController extends BaseController {

    @Autowired
    private JPMService service;

    @RequestMapping(value = "/jpm/{entity}/{instanceId}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ObjectConverterData.ObjectConverterDataItem listObject(
            @PathVariable Entity entity, @PathVariable String instanceId, @RequestParam String textField,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) throws PMException {
        final Field field = entity.getFieldById(textField);
        final Object object = entity.getDao().get(instanceId);
        return new ObjectConverterDataItem(entity.getDao().getId(object).toString(), JPMUtils.get(object, field.getProperty()).toString());
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/show", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("show"));
        getContext().setObject(entity.getDao().get(instanceId));
        getContext().setEntityInstance(newEntityInstance(instanceId, entity));
        return newMav();
    }

    /**
     * GET method prepares form.
     *
     * @param entity
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/add", method = RequestMethod.GET)
    public ModelAndView addPrepare(@PathVariable Entity entity) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("add"));
        getContext().setObject(JPMUtils.newInstance(entity.getClazz()));
        getContext().setEntityInstance(newEntityInstance(null, entity));
        final ModelAndView mav = newMav();
        mav.setViewName("jpm-edit");
        return mav;
    }

    /**
     * POST method finalizes the operation
     *
     * @param entity
     * @return redirect to show
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/add", method = RequestMethod.POST)
    public String addCommit(@PathVariable Entity entity) throws PMException {
        final Operation operation = entity.getOperation("add");
        final String instanceId = getService().save(entity, operation,
                new EntityInstance(null, entity, operation, null),
                getRequest().getParameterMap());
        return "redirect:/jpm/" + entity.getId() + "/" + instanceId + "/show";
    }

    /**
     * GET method prepares form.
     *
     * @param entity
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/{instanceId}/edit", method = RequestMethod.GET)
    public ModelAndView editPrepare(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("edit"));
        getContext().setObject(entity.getDao().get(instanceId));
        getContext().setEntityInstance(newEntityInstance(instanceId, entity));
        return newMav();
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/edit", method = RequestMethod.POST)
    public String editCommit(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("edit"));
        getService().update(
                getContext().getEntity(),
                getContext().getOperation(),
                instanceId,
                newEntityInstance(instanceId, entity),
                getRequest().getParameterMap());
        return "redirect:/jpm/" + entity.getId() + "/" + instanceId + "/show";
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/delete", method = RequestMethod.GET)
    public String delete(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("edit"));
        getService().delete(entity, entity.getOperation("edit"), instanceId);
        return "redirect:/jpm/" + entity.getId();
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}", method = RequestMethod.DELETE)
    public String restDelete(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        return delete(entity, instanceId);
    }

    public JPMService getService() {
        return service;
    }

    public void setService(JPMService service) {
        this.service = service;
    }
}
