package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.EntityInstanceOwner;
import jpaoletti.jpm2.core.model.EntityPath;
import jpaoletti.jpm2.core.model.IdentifiedObject;
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
     * @param entityPath Entity being added
     * @param lastId Id of the latest added instance. Just for repeated
     *
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entityPath}/" + OP_ADD, method = RequestMethod.GET)
    public ModelAndView addPrepare(
            @PathVariable EntityPath entityPath,
            @RequestParam(required = false) String lastId) throws PMException {
        getContext().set(entityPath, OP_ADD);
        //If there is a "lastId" , the object values are used as defaults
        final Object object = (lastId == null) ? JPMUtils.newInstance(entityPath.getEntity().getClazz()) : getService().get(entityPath.getEntity(), lastId).getObject();
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), entityPath.getEntity(), getContext().getOperation()));
        return new ModelAndView("jpm-" + EditController.OP_EDIT);
    }

    /**
     * GET method prepares form.
     *
     * @param ownerPath
     * @param entityPath
     * @param ownerId Id of the owner
     * @param lastId Id of the latest added instance. Just for repeated
     * getContext().getOperation()
     *
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{ownerPath}/{ownerId}/{entityPath}/" + OP_ADD, method = RequestMethod.GET)
    public ModelAndView addWeakPrepare(
            @PathVariable EntityPath ownerPath,
            @PathVariable EntityPath entityPath,
            @PathVariable String ownerId,
            @RequestParam(required = false) String lastId) throws PMException {
        getContext().set(entityPath, OP_ADD);
        //If there is a "lastId" , the object values are used as defaults
        final Object object = (lastId == null) ? JPMUtils.newInstance(getContext().getEntity().getClazz()) : getService().get(getContext().getEntity(), lastId).getObject();
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext().getEntity(), getContext().getOperation(), ownerPath.getEntity()));
        if (getContext().getEntity().isWeak()) {
            getContext().getEntityInstance().setOwner(new EntityInstanceOwner(ownerPath.getEntity(), new IdentifiedObject(ownerId)));
        }
        return new ModelAndView("jpm-" + EditController.OP_EDIT);
    }

    /**
     * POST method finalizes the getContext().getOperation()
     *
     * @param entityPath
     * @param repeat
     * @return redirect to show
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entityPath}/" + OP_ADD, method = RequestMethod.POST)
    public ModelAndView addCommit(
            @PathVariable EntityPath entityPath,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        getContext().set(entityPath, OP_ADD);
        try {
            final IdentifiedObject newObject = getService().save(entityPath.getEntity(), getContext().getOperation(), new EntityInstance(entityPath.getEntity(), getContext().getOperation()), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, entityPath.getEntity(), getContext().getOperation()));
            getContext().setGlobalMessage(MessageFactory.success("jpm.add.success"));
            if (repeat) {
                return new ModelAndView(String.format("redirect:/jpm/%s/%s?repeat=true&lastId=%s", entityPath.getEntity().getId(), OP_ADD, newObject.getId()));
            } else {
                return next(entityPath.getEntity(), getContext().getOperation(), newObject.getId(), ShowController.OP_SHOW);
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return addPrepare(entityPath, null);
        }
    }

    /**
     * POST method finalizes the getContext().getOperation()
     *
     * @param ownerPath
     * @param ownerId
     * @param entityPath
     * @param repeat
     * @return redirect to show
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{ownerPath}/{ownerId}/{entityPath}/add", method = RequestMethod.POST)
    public ModelAndView addWeakCommit(
            @PathVariable EntityPath ownerPath,
            @PathVariable String ownerId,
            @PathVariable EntityPath entityPath,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        try {
            getContext().set(entityPath, OP_ADD);
            final IdentifiedObject newObject = getService().save(ownerPath.getEntity(), ownerId, entityPath.getEntity(), getContext().getOperation(), new EntityInstance(entityPath.getEntity(), getContext().getOperation()), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, entityPath.getEntity(), getContext().getOperation(), ownerPath.getEntity()));
            getContext().setGlobalMessage(MessageFactory.success("jpm.add.success"));
            if (repeat) {
                final EntityInstance instance = getContext().getEntityInstance();
                return new ModelAndView(String.format("redirect:/jpm/%s/%s/%s/%s?repeat=true&lastId=%s", instance.getOwner().getEntity().getId(), instance.getOwnerId(), entityPath.getEntity().getId(), OP_ADD, newObject.getId()));
            } else {
                return next(entityPath.getEntity(), getContext().getOperation(), newObject.getId(), ShowController.OP_SHOW);
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return addWeakPrepare(ownerPath, entityPath, ownerId, null);
        }
    }
}
