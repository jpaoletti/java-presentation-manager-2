package jpaoletti.jpm2.controller;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.FieldValidator;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.ObjectConverterData;
import jpaoletti.jpm2.web.ObjectConverterData.ObjectConverterDataItem;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
public class CrudController extends BaseController {

    @RequestMapping(value = "/jpm/{entity}/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public ObjectConverterData.ObjectConverterDataItem listObject(
            @PathVariable Entity entity, @PathVariable String id, @RequestParam String textField,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) throws PMException {
        final Field field = getEntity().getFieldById(textField);
        final Object object = getEntity().getDao().get(id);
        return new ObjectConverterDataItem(getEntity().getDao().getId(object).toString(),
                JPMUtils.get(object, field.getProperty()).toString());
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/show", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("show"));
        getContext().setObject(getEntity().getDao().get(instanceId));
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
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("add"));
        getContext().setObject(JPMUtils.newInstance(entity.getClazz()));
        getContext().setEntityInstance(newEntityInstance(null, entity));
        try {
            processFields();
            preExecute();
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                //getEntityMessages().add(e.getMsg());
            }
        }
        getEntity().getDao().save(getObject());
        final String instanceId = getEntity().getDao().getId(getObject()).toString();
        postExecute();
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
        getContext().setObject(getEntity().getDao().get(instanceId));
        getContext().setEntityInstance(newEntityInstance(instanceId, entity));
        return newMav();
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/edit", method = RequestMethod.POST)
    @Transactional(readOnly = false)
    public String editCommit(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        editPrepare(entity, instanceId);
        try {
            processFields();
            preExecute();
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                //getEntityMessages().add(e.getMsg());
            }
        }
        getEntity().getDao().update(getObject());
        postExecute();
        return "redirect:/jpm/" + entity.getId() + "/" + instanceId + "/show";
    }

    protected void processFields() throws PMException {
        preConversion();
        for (Map.Entry<String, Object> entry : getContext().getEntityInstance().getValues().entrySet()) {
            final String newValue = getRequest().getParameter("field_" + entry.getKey());
            final Field field = getContext().getEntity().getFieldById(entry.getKey());
            try {
                final Converter converter = field.getConverter(getContext().getOperation());
                final Object convertedValue = converter.build(field, getContext().getObject(), newValue);
                final List<FieldValidator> validators = field.getValidators(getContext().getOperation());
                for (FieldValidator fieldValidator : validators) {
                    final Message msg = fieldValidator.validate(getObject(), convertedValue);
                    if (msg != null) {
                        //addFieldMsg(field, msg);
                    }
                }
                JPMUtils.set(getObject(), field.getProperty(), convertedValue);
            } catch (IgnoreConvertionException e) {
            } catch (ConverterException e) {
                //addFieldMsg(field, e.getMsg());
            }
        }
//        if (!getFieldMessages().isEmpty()) {
//            throw new ValidationException(null);
//        }
    }

    protected ModelAndView newMav() throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-" + getContext().getOperation().getId());
        mav.addObject("entity", getContext().getEntity());
        mav.addObject("operation", getContext().getOperation());
        if (getContext().getObject() != null) {
            mav.addObject("object", getContext().getObject());
        }
        mav.addObject("generalOperations", getContext().getEntity().getOperationsFor(null, getContext().getOperation(), OperationScope.GENERAL));
        mav.addObject("selectedOperations", getContext().getEntity().getOperationsFor(null, getContext().getOperation(), OperationScope.SELECTED));
        if (getContext().getObject() != null) {
            mav.addObject("itemOperations", getContext().getEntity().getOperationsFor(getContext().getObject(), getContext().getOperation(), OperationScope.ITEM));
        }
        if (getContext().getEntityInstance() != null) {
            mav.addObject("instance", getContext().getEntityInstance());
        }
        return mav;
    }
}
