package jpaoletti.jpm2.web.controller;

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
public class AddController extends BaseController {

    public static final String OP_ADD = "add";

    /**
     * GET method prepares form.
     *
     * @param lastId Id of the latest added instance. Just for repeated
     *
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/{operationId:" + OP_ADD + "}", method = RequestMethod.GET)
    public ModelAndView addPrepare(@RequestParam(required = false) String lastId) throws PMException {
        //If there is a "lastId" , the object values are used as defaults
        final Object object = (lastId == null) ? JPMUtils.newInstance(getContext().getEntity().getClazz()) : getService().get(getContext().getEntity(), lastId).getObject();
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext().getEntity(), getContext().getOperation()));
        return new ModelAndView("jpm-" + EditController.OP_EDIT);
    }

    /**
     * GET method prepares form.
     *
     * @param ownerId Id of the owner
     * @param lastId Id of the latest added instance. Just for repeated
     * operation
     *
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/{operationId:" + OP_ADD + "}", method = RequestMethod.GET)
    public ModelAndView addWeakPrepare(@PathVariable String ownerId, @RequestParam(required = false) String lastId) throws PMException {
        final ModelAndView mav = addPrepare(lastId);
        if (getContext().getEntity().isWeak()) {
            getContext().getEntityInstance().setOwner(new EntityInstanceOwner(getContext().getEntity().getOwner().getOwner(), new IdentifiedObject(ownerId)));
        }
        return mav;
    }

    /**
     * POST method finalizes the operation
     *
     * @param repeat
     * @return redirect to show
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/{operationId:" + OP_ADD + "}", method = RequestMethod.POST)
    public ModelAndView addCommit(@RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final Entity entity = getContext().getEntity();
        final Operation operation = getContext().getOperation();
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
            return addPrepare(null);
        }
    }

    /**
     * POST method finalizes the operation
     *
     * @param owner
     * @param ownerId
     * @param repeat
     * @return redirect to show
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{owner}/{ownerId}/{entity}/{operationId:" + OP_ADD + "}", method = RequestMethod.POST)
    public ModelAndView addWeakCommit(@PathVariable Entity owner, @PathVariable String ownerId,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final Entity entity = getContext().getEntity();
        final Operation operation = getContext().getOperation();
        try {
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
            return addWeakPrepare(ownerId, null);
        }
    }
}
