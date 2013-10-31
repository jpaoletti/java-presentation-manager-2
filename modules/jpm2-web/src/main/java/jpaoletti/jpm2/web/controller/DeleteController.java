package jpaoletti.jpm2.web.controller;

import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.EntityPath;
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

    @RequestMapping(value = "/jpm/{entityPath}/{instanceId}/" + OP_DELETE, method = RequestMethod.GET)
    public String delete(@PathVariable EntityPath entityPath, @PathVariable String instanceId) throws PMException {
        getContext().set(entityPath, OP_DELETE);
        final IdentifiedObject iobject = getService().delete(entityPath.getEntity(), getContext().getOperation(), instanceId);
        getContext().setGlobalMessage(MessageFactory.success("jpm.delete.success"));
        final EntityInstance instance = new EntityInstance(iobject, entityPath.getEntity(), getContext().getOperation(), entityPath.getOwner());
        return toList(instance, entityPath.getEntity());
    }

    @RequestMapping(value = "/jpm/{entityPath}/{instanceIds}/" + OP_DELETE_SELECTED, method = RequestMethod.GET)
    public String deleteSelected(@PathVariable EntityPath entityPath, @PathVariable List<String> instanceIds) throws PMException {
        getContext().set(entityPath, OP_DELETE_SELECTED);
        IdentifiedObject iobject = null;
        for (String instanceId : instanceIds) {
            iobject = getService().delete(entityPath.getEntity(), getContext().getOperation(), instanceId);
        }
        getContext().setGlobalMessage(MessageFactory.success("jpm.multidelete.success"));
        return toList(new EntityInstance(iobject, entityPath.getEntity(), getContext().getOperation(), entityPath.getOwner()), entityPath.getEntity());
    }

    @RequestMapping(value = "/jpm/{entityPath}/{instanceId}", method = RequestMethod.DELETE)
    public String restDelete(@PathVariable EntityPath entityPath, @PathVariable String instanceId) throws PMException {
        return delete(entityPath, instanceId);
    }
}
