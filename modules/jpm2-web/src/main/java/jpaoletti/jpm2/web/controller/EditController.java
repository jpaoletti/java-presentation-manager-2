package jpaoletti.jpm2.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for edit operations.
 *
 * @author jpaoletti
 */
@Controller
public class EditController extends BaseController {

    public static final String OP_EDIT = "edit";
    public static final String OP_INLINE_EDIT = "iledit";

    /**
     * GET method prepares form.
     *
     * @param instanceId
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_EDIT + "}", method = RequestMethod.GET)
    public ModelAndView editPrepare(@PathVariable String instanceId) throws PMException {
        final IdentifiedObject iobject = getJpm().getService().get(getContext().getEntity(), getContext().getOperation(), instanceId);
        getContext().setEntityInstance(new EntityInstance(iobject, getContext().getEntity(), getContext().getOperation()));
        return new ModelAndView("jpm-" + OP_EDIT);
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_EDIT + "}", method = RequestMethod.POST)
    public ModelAndView editCommit(@PathVariable String instanceId, @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        try {
            final EntityInstance instance = new EntityInstance(new IdentifiedObject(instanceId), getContext().getEntity(), getContext().getOperation());
            getContext().setEntityInstance(instance);
            getJpm().getService().update(getContext().getEntity(), getContext().getOperation(), instance, getRequest().getParameterMap());
            if (repeat) {
                return new ModelAndView(String.format("redirect:/jpm/%s/%s/%s?repeat=true", getContext().getEntity().getId(), instanceId, OP_EDIT));
            } else {
                return next(getContext().getEntity(), getContext().getOperation(), instanceId, ShowController.OP_SHOW);
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return editPrepare(instanceId);
        }
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/" + OP_INLINE_EDIT, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> ileditCommit(
            @PathVariable Entity entity,
            @PathVariable String instanceId,
            @RequestParam() String name,
            @RequestParam() String value) throws PMException {
        final Operation operation = entity.getOperation(OP_EDIT);
        getContext().set(entity, operation);
        try {
            final EntityInstance instance = new EntityInstance(new IdentifiedObject(instanceId), entity, operation);
            getContext().setEntityInstance(instance);
            final Map<String, String[]> params = new HashMap<>();
            params.put("field_" + name, (String[]) Arrays.asList(value).toArray());
            final Object tmp = instance.getValues().get(name);
            instance.getValues().clear();
            instance.getValues().put(name, tmp);
            getJpm().getService().update(entity, operation, instance, params);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ValidationException e) {
            final StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<Message>> entry : getContext().getFieldMessages().entrySet()) {
                for (Message message : entry.getValue()) {
                    sb.append(getInternationalizedMessage(message)).append(". ");
                }
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}
