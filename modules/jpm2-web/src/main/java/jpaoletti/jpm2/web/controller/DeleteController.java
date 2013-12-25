package jpaoletti.jpm2.web.controller;

import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
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

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/{operationId:" + OP_DELETE + "}", method = RequestMethod.GET)
    public String delete(@PathVariable String instanceId) throws PMException {
        final IdentifiedObject iobject = initItemControllerOperation(instanceId);
        getService().delete(getContext().getEntity(), getContext().getEntityContext(), getContext().getOperation(), instanceId);
        getContext().setGlobalMessage(MessageFactory.success("jpm.delete.success"));
        return toList(new EntityInstance(iobject, getContext()), getContext().getEntity());
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceIds}/{operationId:" + OP_DELETE_SELECTED + "}", method = RequestMethod.GET)
    public String deleteSelected(@PathVariable List<String> instanceIds) throws PMException {
        IdentifiedObject iobject = null;
        for (String instanceId : instanceIds) {
            iobject = initItemControllerOperation(instanceId);
            getService().delete(getContext().getEntity(), getContext().getEntityContext(), getContext().getOperation(), instanceId);
        }
        getContext().setGlobalMessage(MessageFactory.success("jpm.multidelete.success"));
        return toList(new EntityInstance(iobject, getContext()), getContext().getEntity());
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}", method = RequestMethod.DELETE)
    public String restDelete(@PathVariable String instanceId) throws PMException {
        return delete(instanceId);
    }
}
