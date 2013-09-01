package jpaoletti.jpm2.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import jpaoletti.jpm2.core.service.JPMService;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.ValidationException;
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
        final Object object = getService().get(entity, instanceId);
        return new ObjectConverterDataItem(entity.getDao().getId(object).toString(), JPMUtils.get(object, field.getProperty()).toString());
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/show", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public Map<String, Object> showJSON(@PathVariable Entity entity, @PathVariable String instanceId, @RequestParam(required = false) String fields) throws PMException {
        getContext().setOperation(entity.getOperation("show"));
        final Object object = getService().get(entity, getContext().getOperation(), instanceId);
        getContext().setObject(object);
        final Map<String, Object> values = new LinkedHashMap<>();
        final String[] fs = fields.split("[,]");
        for (String fid : fs) {
            final Field field = entity.getFieldById(fid);
            final Converter converter = field.getConverter(getContext().getOperation());
            if (converter != null) {
                try {
                    values.put(field.getTitle(entity), converter.visualize(field, object));
                } catch (IgnoreConvertionException ex) {
                }
            }
        }
        return values;
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/show", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("show"));
        getContext().setObject(getService().get(entity, getContext().getOperation(), instanceId));
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
    @RequestMapping(value = {"/jpm/{entity}/add", "/jpm/{owner}/{ownerId}/{entity}/add"}, method = RequestMethod.GET)
    public ModelAndView addPrepare(@PathVariable Entity entity, @PathVariable String ownerId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("add"));
        getContext().setObject(JPMUtils.newInstance(entity.getClazz()));
        getContext().setEntityInstance(newEntityInstance(null, entity));
        final ModelAndView mav = newMav();
        mav.setViewName("jpm-edit");
        if (entity.isWeak()) {
            mav.addObject("ownerId", ownerId);
        }
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
    public ModelAndView addCommit(@PathVariable Entity entity) throws PMException {
        final Operation operation = entity.getOperation("add");
        try {
            final String instanceId = getService().save(entity, operation,
                    new EntityInstance(null, entity, operation, null),
                    getRequest().getParameterMap());
            return new ModelAndView("redirect:/jpm/" + entity.getId() + "/" + instanceId + "/show");
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return addPrepare(entity, null);
        }
    }

    /**
     * POST method finalizes the operation
     *
     * @param entity
     * @return redirect to show
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/add", method = RequestMethod.POST)
    public ModelAndView addWeakCommit(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity) throws PMException {
        final Operation operation = entity.getOperation("add");
        try {
            final String instanceId = getService().save(owner, ownerId, entity, operation,
                    new EntityInstance(null, entity, operation, null),
                    getRequest().getParameterMap());
            return new ModelAndView("redirect:/jpm/" + entity.getId() + "/" + instanceId + "/show");
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return addPrepare(entity, ownerId);
        }
    }

    public JPMService getService() {
        return service;
    }

    public void setService(JPMService service) {
        this.service = service;
    }
}
