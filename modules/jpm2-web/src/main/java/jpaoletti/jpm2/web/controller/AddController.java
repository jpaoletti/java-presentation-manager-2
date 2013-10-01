package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
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
public class AddController extends BaseController {

    public static final String OP_ADD = "add";

    /**
     * GET method prepares form.
     *
     * @param entity
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/" + OP_ADD, method = RequestMethod.GET)
    public ModelAndView addPrepare(@PathVariable Entity entity) throws PMException {
        final Operation operation = entity.getOperation(OP_ADD);
        getContext().set(entity, operation);
        final Object object = JPMUtils.newInstance(entity.getClazz());
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), entity, operation));
        return new ModelAndView("jpm-" + EditController.OP_EDIT);
    }

    /**
     * GET method prepares form.
     *
     * @param entity
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/" + OP_ADD, method = RequestMethod.GET)
    public ModelAndView addWeakPrepare(@PathVariable Entity entity, @PathVariable String ownerId) throws PMException {
        final ModelAndView mav = addPrepare(entity);
        if (entity.isWeak()) {
            getContext().getEntityInstance().setOwnerId(ownerId);
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
    @RequestMapping(value = "/jpm/{entity}/" + OP_ADD, method = RequestMethod.POST)
    public ModelAndView addCommit(@PathVariable Entity entity) throws PMException {
        final Operation operation = entity.getOperation(OP_ADD);
        getContext().set(entity, operation);
        try {
            final IdentifiedObject newObject = getService().save(entity, operation, new EntityInstance(entity, operation), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, entity, operation));
            return next(entity, operation, newObject.getId(), ShowController.OP_SHOW);
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
            final IdentifiedObject newObject = getService().save(owner, ownerId, entity, operation, new EntityInstance(entity, operation), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, entity, operation));
            return next(entity, operation, newObject.getId(), "show");
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return addWeakPrepare(entity, ownerId);
        }
    }
}
