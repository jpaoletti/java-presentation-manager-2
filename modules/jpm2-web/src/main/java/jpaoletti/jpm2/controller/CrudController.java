package jpaoletti.jpm2.controller;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.FieldValidator;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class CrudController extends BaseController {

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/show", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("show"));
        getContext().setObject(getEntity().getDao().get(instanceId));
        final ModelAndView mav = newMav();
        mav.addObject("instance", newEntityInstance(instanceId, entity));
        return mav;
    }

    /**
     * GET method prepares form.
     *
     * @param entity
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/add", method = RequestMethod.GET)
    public ModelAndView add(@PathVariable Entity entity) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("add"));
        getContext().setObject(JPMUtils.newInstance(entity.getClazz()));
        final ModelAndView mav = newMav();
        mav.setViewName("jpm-edit");
        final Operation operation = getContext().getOperation();
        final EntityInstance instance = new EntityInstance(null, entity, operation, getContext().getObject());
        mav.addObject("instance", instance);
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
        final EntityInstance instance = newEntityInstance(null, entity);
        try {
            processFields(instance);
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

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/edit")
    public ModelAndView edit(@PathVariable Entity entity, @PathVariable String operationId, @PathVariable String instanceId) throws PMException {
        return null;
    }

    protected void processFields(EntityInstance instance) throws PMException {
        for (Map.Entry<String, Object> entry : instance.getValues().entrySet()) {
            final String newValue = getRequest().getParameter("field_" + entry.getKey());
            final Field field = getContext().getEntity().getFieldById(entry.getKey());
            preConversion();
            try {
                final Converter converter = field.getConverter(getContext().getOperation());
                final Object convertedValue = converter.build(field, newValue);
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
        mav.addObject("object", getContext().getObject());
        mav.addObject("generalOperations", getContext().getEntity().getOperationsFor(null, getContext().getOperation(), OperationScope.GENERAL));
        mav.addObject("selectedOperations", getContext().getEntity().getOperationsFor(null, getContext().getOperation(), OperationScope.SELECTED));
        return mav;
    }
}
