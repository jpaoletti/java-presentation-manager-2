package jpaoletti.jpm2.web.controller;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.EntityInstanceOwner;
import jpaoletti.jpm2.core.model.EntityOwner;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.JPMAskConfirmationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
     * @param close closes the page after adding succesful
     *
     * @return model and view
     * @throws PMException
     */
    @GetMapping(value = "/jpm/{entity}/{operationId:" + OP_ADD + "}")
    public ModelAndView addPrepare(
            @RequestParam(required = false) String lastId,
            @RequestParam(required = false, defaultValue = "false") boolean close) throws PMException {
        LOG.debug("addPrepare IN entity={} op={} lastId={} close={}", getContext().getEntity(), getContext().getOperation(), lastId, close);
        //If there is a "lastId" , the object values are used as defaults
        final Object object = getDefaultsObject(lastId, null);
        final Operation operation = getContext().getOperation();
        if (operation.getContext() != null) {
            operation.getContext().preConversion(object);
        }
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext()));
        checkOperationCondition(getContext().getOperation(), getContext().getEntityInstance());
        final ModelAndView mav = new ModelAndView("op-edit");
        mav.addObject("close", close);
        return mav;
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
    @GetMapping(value = "/jpm/{owner}/{ownerId}/{entity}/{operationId:" + OP_ADD + "}")
    public ModelAndView addWeakPrepare(@PathVariable String ownerId, @RequestParam(required = false) String lastId) throws PMException {
        LOG.debug("addWeakPrepare IN entity={} op={} ownerId={} lastId={}", getContext().getEntity(), getContext().getOperation(), ownerId, lastId);
        final Object object = getDefaultsObject(lastId, ownerId);
        IdentifiedObject iobjectOwner = null;
        if (getContext().getEntity().isWeak(getContext().getEntityContext())) {
            iobjectOwner = getOwnerObject(ownerId);
            getContext().getEntity().getOwner().setOwnerObject(getContext().getEntityContext(), object, iobjectOwner.getObject());
        }
        final Operation operation = getContext().getOperation();
        if (operation.getContext() != null) {
            operation.getContext().preConversion(object);
        }
        getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext()));
        if (iobjectOwner != null) {
            getContext().getEntityInstance().setOwner(new EntityInstanceOwner(getContext().getEntity().getOwner(getContext().getEntityContext()).getOwner(), iobjectOwner));
        }
        final ModelAndView mav = new ModelAndView("op-edit");
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
    @PostMapping(value = "/jpm/{entity}/{operationId:" + OP_ADD + "}")
    @ResponseBody
    public JPMPostResponse addCommit(@RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final Entity entity = getContext().getEntity();
        final Operation operation = getContext().getOperation();
        LOG.debug("addCommit IN entity={} op={} repeat={}", entity, operation, repeat);
        if (entity.isWeak(getContext().getEntityContext()) && !entity.getOwner(getContext().getEntityContext()).isOptional()) {
            throw new NotAuthorizedException();
        }
        try {
            // Same instance addPrepare checks the condition with (preConversion included)
            final Object conditionObject = JPMUtils.newInstance(entity.getClazz());
            if (operation.getContext() != null) {
                operation.getContext().preConversion(conditionObject);
            }
            checkOperationCondition(operation, new EntityInstance(new IdentifiedObject(null, conditionObject), getContext()));
            final IdentifiedObject newObject = getService().save(entity, getContext().getEntityContext(), operation, new EntityInstance(getContext()), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, getContext()));
            getContext().setGlobalMessage(MessageFactory.success(getSuccessMsg(operation)));
            LOG.debug("addCommit OUT entity={} newId={} repeat={}", entity, newObject.getId(), repeat);
            if (repeat) {
                if (operation.getConfig("clear-on-repeat", "false").equalsIgnoreCase("true")) {
                    return new JPMPostResponse(true, buildRedirect(entity, null, OP_ADD, "repeated=true"), MessageFactory.success(getSuccessMsg(operation)));
                } else {
                    return new JPMPostResponse(true, buildRedirect(entity, null, OP_ADD, "repeated=true&lastId=" + newObject.getId()), MessageFactory.success(getSuccessMsg(operation)));
                }
            } else {
                return new JPMPostResponse(true, next(entity, operation, newObject.getId(), ShowController.OP_SHOW).getViewName(), MessageFactory.success(getSuccessMsg(operation)));
            }
        } catch (ValidationException e) {
            LOG.debug("addCommit entity={} VALIDATION FAILED fieldMsgs={}", entity, getContext().getFieldMessages().keySet());
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            final Object object = e.getValidatedObject();
            getContext().setEntityInstance(new EntityInstance(new IdentifiedObject(null, object), getContext()));

            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (JPMAskConfirmationException e) {
            if (e.getMsg() != null) {
                return new JPMPostResponse(false, null).askConfirmation(e.getMsg());
            } else {
                return new JPMPostResponse(false, null).askConfirmation(MessageFactory.error(e.getMessage()));
            }
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

    private static String getSuccessMsg(final Operation operation) {
        return operation.getConfig("add-success-msg", "jpm.add.success");
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
    @PostMapping(value = "/jpm/{owner}/{ownerId}/{entity}/{operationId:" + OP_ADD + "}")
    @ResponseBody
    public JPMPostResponse addWeakCommit(@PathVariable Entity owner, @PathVariable String ownerId,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final Entity entity = getContext().getEntity();
        final Operation operation = getContext().getOperation();
        LOG.debug("addWeakCommit IN owner={} ownerId={} entity={} op={} repeat={}", owner, ownerId, entity, operation, repeat);
        if (!entity.isWeak(getContext().getEntityContext()) || !entity.getOwner(getContext().getEntityContext()).getOwner().getId().equals(owner.getId())) {
            throw new NotAuthorizedException();
        }
        try {
            final IdentifiedObject iobjectOwner = getOwnerObject(ownerId);
            final Object conditionObject = JPMUtils.newInstance(entity.getClazz());
            entity.getOwner(getContext().getEntityContext()).setOwnerObject(getContext().getEntityContext(), conditionObject, iobjectOwner.getObject());
            // Same instance addWeakPrepare checks the condition with (preConversion included)
            if (operation.getContext() != null) {
                operation.getContext().preConversion(conditionObject);
            }
            final EntityInstance conditionInstance = new EntityInstance(new IdentifiedObject(null, conditionObject), getContext());
            conditionInstance.setOwner(new EntityInstanceOwner(entity.getOwner(getContext().getEntityContext()).getOwner(), iobjectOwner));
            checkOperationCondition(operation, conditionInstance);
            final IdentifiedObject newObject = getService().save(owner, ownerId, entity, getContext().getEntityContext(), operation, new EntityInstance(getContext()), getRequest().getParameterMap());
            getContext().setEntityInstance(new EntityInstance(newObject, getContext()));
            getContext().setGlobalMessage(MessageFactory.success(getSuccessMsg(operation)));
            if (repeat) {
                final EntityInstance instance = getContext().getEntityInstance();
                if (operation.getConfig("clear-on-repeat", "false").equalsIgnoreCase("true")) {
                    return new JPMPostResponse(true, buildRedirect(instance.getOwner().getEntity(), instance.getOwnerId(), entity, null, OP_ADD, "repeated=true"), MessageFactory.success(getSuccessMsg(operation)));
                } else {
                    return new JPMPostResponse(true, buildRedirect(instance.getOwner().getEntity(), instance.getOwnerId(), entity, null, OP_ADD, "repeated=true&lastId=" + newObject.getId()), MessageFactory.success(getSuccessMsg(operation)));
                }
            } else {
                return new JPMPostResponse(true, next(entity, operation, newObject.getId(), ShowController.OP_SHOW).getViewName(), MessageFactory.success(getSuccessMsg(operation)));
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
        } catch (JPMAskConfirmationException e) {
            if (e.getMsg() != null) {
                return new JPMPostResponse(false, null).askConfirmation(e.getMsg());
            } else {
                return new JPMPostResponse(false, null).askConfirmation(MessageFactory.error(e.getMessage()));
            }
        } catch (PMException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        }
    }

    /**
     * Loads the owner of a weak entity. A missing owner is not authorized.
     */
    protected IdentifiedObject getOwnerObject(String ownerId) throws PMException {
        final Entity ownerEntity = getContext().getEntity().getOwner(getContext().getEntityContext()).getOwner();
        final IdentifiedObject iobjectOwner = getService().get(ownerEntity, getContext().getEntityContext(), ownerId);
        if (iobjectOwner.getObject() == null) {
            throw new NotAuthorizedException();
        }
        return iobjectOwner;
    }

    /**
     * New instance to prepare the add form. When "lastId" is given (repeated
     * add) its values are used as defaults, but only if the user can see that
     * instance (show operation, when defined) and, for weak entities, it
     * belongs to the same owner. Otherwise it is ignored.
     */
    protected Object getDefaultsObject(String lastId, String ownerId) throws PMException {
        final Entity entity = getContext().getEntity();
        if (lastId != null) {
            try {
                final Operation show = entity.getOperationWithoutAuth(ShowController.OP_SHOW, entity.getContext(getContext().getEntityContext()));
                if (show != null) {
                    show.checkAuthorization(entity, getContext().getEntityContext());
                }
                final Object last = getService().get(entity, getContext().getEntityContext(), lastId).getObject();
                if (last != null && (ownerId == null || isOwnedBy(last, ownerId))) {
                    return last;
                }
            } catch (NotAuthorizedException ex) {
                LOG.debug("getDefaultsObject lastId={} ignored: not authorized", lastId);
            }
        }
        return JPMUtils.newInstance(entity.getClazz());
    }

    private boolean isOwnedBy(Object object, String ownerId) throws PMException {
        final EntityOwner entityOwner = getContext().getEntity().getOwner(getContext().getEntityContext());
        if (entityOwner == null) {
            return true;
        }
        final Object value = JPMUtils.get(object, entityOwner.getLocalProperty());
        if (value == null) {
            return false;
        }
        final Object id = entityOwner.isOnlyId() ? value : entityOwner.getOwner().getDao(getContext().getEntityContext()).getId(value);
        return ownerId.equals(String.valueOf(id));
    }
}
