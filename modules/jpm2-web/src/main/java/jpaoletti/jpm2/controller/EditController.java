package jpaoletti.jpm2.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.core.service.JPMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for edit operations.
 *
 * @author jpaoletti
 */
@Controller
public class EditController extends BaseController {

    @Autowired
    private JPMService service;

    /**
     * GET method prepares form.
     *
     * @param entity
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/{instanceId}/edit", method = RequestMethod.GET)
    public ModelAndView editPrepare(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        prepareItemOperation(entity, instanceId, "edit");
        return newMav();
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceId}/edit", method = RequestMethod.POST)
    public ModelAndView editCommit(@PathVariable Entity entity, @PathVariable String instanceId) throws PMException {
        getContext().setEntity(entity);
        getContext().setOperation(entity.getOperation("edit"));
        try {
            getService().update(
                    getContext().getEntity(),
                    getContext().getOperation(),
                    instanceId,
                    newEntityInstance(instanceId, entity),
                    getRequest().getParameterMap());
            return new ModelAndView("redirect:/jpm/" + entity.getId() + "/" + instanceId + "/show");
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return editPrepare(entity, instanceId);
        }
    }

    public JPMService getService() {
        return service;
    }

    public void setService(JPMService service) {
        this.service = service;
    }
}
