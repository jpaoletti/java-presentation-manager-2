package jpaoletti.jpm2.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.core.service.JPMService;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AddController extends BaseController {

    @Autowired
    private JPMService service;

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
     * GET method prepares form.
     *
     * @param entity
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/add", method = RequestMethod.GET)
    public ModelAndView addWeakPrepare(@PathVariable Entity entity, @PathVariable String ownerId) throws PMException {
        final ModelAndView mav = addPrepare(entity);
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
        getContext().setEntity(entity);
        getContext().setOperation(operation);
        try {
            final String instanceId = getService().save(entity, operation,
                    new EntityInstance(null, entity, operation, null),
                    getRequest().getParameterMap());
            return new ModelAndView("redirect:/jpm/" + entity.getId() + "/" + instanceId + "/show");
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return addPrepare(entity);
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
            return addWeakPrepare(entity, ownerId);
        }
    }

    public JPMService getService() {
        return service;
    }

    public void setService(JPMService service) {
        this.service = service;
    }
}
