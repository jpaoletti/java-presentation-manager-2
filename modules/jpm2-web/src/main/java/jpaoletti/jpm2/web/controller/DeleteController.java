package jpaoletti.jpm2.web.controller;

import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for delete operations.
 *
 * @author jpaoletti
 */
@Controller
public class DeleteController extends BaseController {

    public static final String OP_DELETE = "delete";
    public static final String OP_DELETE_SELECTED = "deleteSelected";

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/delete", method = RequestMethod.GET)
    public String delete(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        final Operation operation = entity.getOperation(OP_DELETE);
        getContext().set(entity, operation);
        final IdentifiedObject iobject = getService().delete(entity, getContext().getOperation(), instanceId);
        getContext().setGlobalMessage(MessageFactory.success("jpm.delete.success"));
        return toList(new EntityInstance(iobject, entity, operation), entity);
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceIds}/deleteSelected", method = RequestMethod.GET)
    public String deleteSelected(@PathVariable Entity entity, @PathVariable List<String> instanceIds) throws PMException {
        final Operation operation = entity.getOperation(OP_DELETE_SELECTED);
        getContext().set(entity, operation);
        IdentifiedObject iobject = null;
        for (String instanceId : instanceIds) {
            iobject = getService().delete(entity, operation, instanceId);
        }
        getContext().setGlobalMessage(MessageFactory.success("jpm.multidelete.success"));
        return toList(new EntityInstance(iobject, entity, operation), entity);
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}", method = RequestMethod.DELETE)
    public String restDelete(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        return delete(entity, instanceId);
    }
}
