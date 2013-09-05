package jpaoletti.jpm2.controller;

import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.service.JPMService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JPMService service;

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/delete", method = RequestMethod.GET)
    public String delete(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("delete"));
        final Object object = getService().get(entity, instanceId);
        getContext().setObject(object);
        final EntityInstance instance = newEntityInstance(instanceId, entity);
        getService().delete(entity, getContext().getOperation(), instanceId);
        return toList(instance, entity);
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceIds}/deleteSelected", method = RequestMethod.GET)
    public String deleteSelected(@PathVariable Entity entity, @PathVariable List<String> instanceIds) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("deleteSelected"));
        EntityInstance instance = null;
        for (String instanceId : instanceIds) {
            final Object object = getService().get(entity, instanceId);
            getContext().setObject(object);
            instance = newEntityInstance(instanceId, entity);
            getService().delete(entity, getContext().getOperation(), instanceId);
        }
        return toList(instance, entity);
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
