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

/**
 * Controller for edit operations.
 *
 * @author jpaoletti
 */
@Controller
public class EditController extends BaseController {

    public static final String OP_EDIT = "edit";

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/iledit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> ileditCommit(
            @PathVariable Entity entity,
            @PathVariable String instanceId,
            @RequestParam() String name,
            @RequestParam() String value) throws PMException {
        final Operation operation = entity.getOperation(OP_EDIT, getContext().getContext());
        getContext().set(entity, operation);
        try {
            final EntityInstance instance = new EntityInstance(new IdentifiedObject(instanceId), getContext());
            getContext().setEntityInstance(instance);
            final Map<String, String[]> params = new HashMap<>();
            params.put("field_" + name, (String[]) Arrays.asList(value).toArray(new String[1]));
            final Object tmp = instance.getValues().get(name);
            instance.getValues().clear();
            instance.getValues().put(name, tmp);
            getJpm().getService().update(entity, getContext().getEntityContext(), operation, instance, params);
            return new ResponseEntity<>(value, HttpStatus.OK);
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
