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
import org.springframework.web.bind.annotation.ResponseBody;
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
        final Object object = (lastId == null) ? JPMUtils.newInstance(getContext().getEntity().getClazz()) : getService().get(getContext().getEntity(), getContext().getEntityContext(), lastId).getObject();
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext()));
        checkOperationCondition(getContext().getOperation(), getContext().getEntityInstance());
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
        final Object object = (lastId == null) ? JPMUtils.newInstance(getContext().getEntity().getClazz()) : getService().get(getContext().getEntity(), getContext().getEntityContext(), lastId).getObject();
        IdentifiedObject iobjectOwner = null;
        if (getContext().getEntity().isWeak(getContext().getEntityContext())) {
            iobjectOwner = getService().get(getContext().getEntity().getOwner(getContext().getEntityContext()).getOwner(), getContext().getEntityContext(), ownerId);
            JPMUtils.set(object, getContext().getEntity().getOwner().getLocalProperty(), iobjectOwner.getObject());
        }
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext()));
        if (iobjectOwner != null) {
            getContext().getEntityInstance().setOwner(new EntityInstanceOwner(getContext().getEntity().getOwner(getContext().getEntityContext()).getOwner(), iobjectOwner));
        }
        final ModelAndView mav = new ModelAndView("jpm-" + EditController.OP_EDIT);
        checkOperationCondition(getContext().getOperation(), getContext().getEntityInstance());
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
    @ResponseBody
    public JPMPostResponse addCommit(@RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final Entity entity = getContext().getEntity();
        final Operation operation = getContext().getOperation();
        try {
            final IdentifiedObject newObject = getService().save(entity, getContext().getEntityContext(), operation, new EntityInstance(getContext()), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, getContext()));
            getContext().setGlobalMessage(MessageFactory.success("jpm.add.success"));
            if (repeat) {
                return new JPMPostResponse(true, buildRedirect(entity, null, OP_ADD, "repeated=true&lastId=" + newObject.getId()), MessageFactory.success("jpm.add.success"));
            } else {
                return new JPMPostResponse(true, next(entity, operation, newObject.getId(), ShowController.OP_SHOW).getViewName(), MessageFactory.success("jpm.add.success"));
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            final Object object = e.getValidatedObject();
            getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext()));

            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (PMException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (Exception e) {
            JPMUtils.getLogger().error("Unexpected error in add commit", e);
            throw e;
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
    @ResponseBody
    public JPMPostResponse addWeakCommit(@PathVariable Entity owner, @PathVariable String ownerId,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final Entity entity = getContext().getEntity();
        final Operation operation = getContext().getOperation();
        try {
            final IdentifiedObject newObject = getService().save(owner, ownerId, entity, getContext().getEntityContext(), operation, new EntityInstance(getContext()), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, getContext()));
            getContext().setGlobalMessage(MessageFactory.success("jpm.add.success"));
            if (repeat) {
                final EntityInstance instance = getContext().getEntityInstance();
                return new JPMPostResponse(true, buildRedirect(instance.getOwner().getEntity(), instance.getOwnerId(), entity, null, OP_ADD, "repeated=true&lastId=" + newObject.getId()), MessageFactory.success("jpm.add.success"));
            } else {
                return new JPMPostResponse(true, next(entity, operation, newObject.getId(), ShowController.OP_SHOW).getViewName(), MessageFactory.success("jpm.add.success"));
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            final Object object = e.getValidatedObject();
            getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext()));
            if (getContext().getEntity().isWeak(getContext().getEntityContext())) {
                getContext().getEntityInstance().setOwner(new EntityInstanceOwner(entity.getOwner(getContext().getEntityContext()).getOwner(), new IdentifiedObject(ownerId)));
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (PMException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        }
    }
}
