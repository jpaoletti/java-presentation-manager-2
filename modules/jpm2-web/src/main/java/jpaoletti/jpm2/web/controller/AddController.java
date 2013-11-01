package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.web.controller.interfaces.IAddController;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.EntityInstanceOwner;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class AddController extends BaseController implements IAddController {

    /**
     * GET method prepares form.
     *
     * @param entity Entity being added
     * @param lastId Id of the latest added instance. Just for repeated
     *
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/" + OP_ADD, method = RequestMethod.GET)
    @Override
    public ModelAndView addPrepare(
            @PathVariable Entity entity,
            @RequestParam(required = false) String lastId) throws PMException {
        final Operation operation = entity.getOperation(OP_ADD);
        getContext().set(entity, operation);
        //If there is a "lastId" , the object values are used as defaults
        final Object object = (lastId == null) ? JPMUtils.newInstance(entity.getClazz()) : getService().get(entity, lastId).getObject();
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), entity, operation));
        return new ModelAndView("jpm-" + EditController.OP_EDIT);
    }

    /**
     * GET method prepares form.
     *
     * @param entity Entity being added
     * @param ownerId Id of the owner
     * @param lastId Id of the latest added instance. Just for repeated
     * operation
     *
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/" + OP_ADD, method = RequestMethod.GET)
    @Override
    public ModelAndView addWeakPrepare(
            @PathVariable Entity entity,
            @PathVariable String ownerId,
            @RequestParam(required = false) String lastId) throws PMException {
        final ModelAndView mav = addPrepare(entity, lastId);
        if (entity.isWeak()) {
            getContext().getEntityInstance().setOwner(new EntityInstanceOwner(entity.getOwner().getOwner(), new IdentifiedObject(ownerId)));
        }
        return mav;
    }

    /**
     * POST method finalizes the operation
     *
     * @param entity
     * @param repeat
     * @return redirect to show
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/" + OP_ADD, method = RequestMethod.POST)
    @Override
    public ModelAndView addCommit(
            @PathVariable Entity entity,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final Operation operation = entity.getOperation(OP_ADD);
        getContext().set(entity, operation);
        try {
            final IdentifiedObject newObject = getService().save(entity, operation, new EntityInstance(entity, operation), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, entity, operation));
            getContext().setGlobalMessage(MessageFactory.success("jpm.add.success"));
            if (repeat) {
                return new ModelAndView(String.format("redirect:/jpm/%s/%s?repeat=true&lastId=%s", entity.getId(), OP_ADD, newObject.getId()));
            } else {
                return next(entity, operation, newObject.getId(), ShowController.OP_SHOW);
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return addPrepare(entity, null);
        }
    }

    /**
     * POST method finalizes the operation
     *
     * @param owner
     * @param ownerId
     * @param entity
     * @param repeat
     * @return redirect to show
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/add", method = RequestMethod.POST)
    @Override
    public ModelAndView addWeakCommit(
            @PathVariable Entity owner,
            @PathVariable String ownerId,
            @PathVariable Entity entity,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final Operation operation = entity.getOperation("add");
        try {
            getContext().set(entity, operation);
            final IdentifiedObject newObject = getService().save(owner, ownerId, entity, operation, new EntityInstance(entity, operation), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, entity, operation));
            getContext().setGlobalMessage(MessageFactory.success("jpm.add.success"));
            if (repeat) {
                final EntityInstance instance = getContext().getEntityInstance();
                return new ModelAndView(String.format("redirect:/jpm/%s/%s/%s/%s?repeat=true&lastId=%s", instance.getOwner().getEntity().getId(), instance.getOwnerId(), entity.getId(), OP_ADD, newObject.getId()));
            } else {
                return next(entity, operation, newObject.getId(), ShowController.OP_SHOW);
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return addWeakPrepare(entity, ownerId, null);
        }
    }
}
