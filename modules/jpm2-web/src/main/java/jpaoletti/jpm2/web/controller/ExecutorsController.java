package jpaoletti.jpm2.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.AsynchronicOperationExecutor;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.OperationExecutor;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for generic executors.
 *
 * @author jpaoletti
 */
@Controller
public class ExecutorsController extends BaseController implements Observer {

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * GET method prepares form.
     *
     * @param instanceIds
     * @return model and view
     * @throws PMException
     */
    @RequestMapping(value = "/jpm/{entity}/{instanceIds}/{operationId}.exec", method = RequestMethod.GET)
    public ModelAndView executorsPrepare(@PathVariable List<String> instanceIds) throws PMException {
        final List<EntityInstance> instances = new ArrayList<>();
        for (String instanceId : instanceIds) {
            initItemControllerOperation(instanceId);
            instances.add(getContext().getEntityInstance());
        }
        final Map<String, Object> preparation = getExecutor().prepare(instances);
        if (getExecutor().immediateExecute()) {
            executorsCommit(instanceIds, false);
            getContext().setGlobalMessage(MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success"));
            return next(getContext().getEntity(), getContext().getOperation(), StringUtils.join(instanceIds, ","), getExecutor().getDefaultNextOperationId());
        } else {
            final ModelAndView mav = new ModelAndView("op-" + getContext().getOperation().getId());
            preparation.entrySet().stream().forEach(
                    e -> mav.addObject(e.getKey(), e.getValue())
            );
            getRequest().getParameterMap().keySet().stream().forEach(
                    key -> mav.addObject((String) key, (String[]) getRequest().getParameterValues((String) key))
            );
            return mav;
        }
    }

    @RequestMapping(value = "/jpm/{entity}/{instanceIds}/{operationId}.exec", method = RequestMethod.POST)
    @ResponseBody
    public JPMPostResponse executorsCommit(@PathVariable List<String> instanceIds, @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        try {
            final List<EntityInstance> instances = new ArrayList<>();
            for (String instanceId : instanceIds) {
                initItemControllerOperation(instanceId);
                instances.add(getContext().getEntityInstance());
            }
            if (getContext().getOperation().isSynchronic()) {
                getExecutor().execute(getContext(), instances, getRequest().getParameterMap(), new Progress());
            } else if (!getJpm().registerAsynchronicExecutor(getContext(), getExecutor(), instances, getRequest().getParameterMap())) {
                throw new PMException("unable.to.register.asynchronic.executor");
            }
            String buildRedirect;
            if (repeat) {
                buildRedirect = buildRedirect(null, null, getContext().getEntity(), StringUtils.join(instanceIds, ","), getContext().getOperation().getId(), "repeated=true");
            } else {
                //BUG follows list in weak not working
                buildRedirect = next(getContext().getEntity(), getContext().getOperation(), StringUtils.join(instanceIds, ","), getExecutor().getDefaultNextOperationId()).getViewName();
            }
            return new JPMPostResponse(true, buildRedirect, MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success"));
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            final IdentifiedObject iobject = new IdentifiedObject(StringUtils.join(instanceIds, ","), e.getValidatedObject());
            getContext().setEntityInstance(new EntityInstance(iobject, getContext()));
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (PMException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        }
    }

    @MessageMapping("/asynchronicOperationExecutorProgress")
    public void asynchronicOperationExecutorProgress(MsgId message) throws Exception {
        System.out.println("jpaoletti.jpm2.web.controller.ExecutorsController.asynchronicOperationExecutorProgress(): " + message);
        final String id = message.toString();
        if (getJpm().getAsynchronicOperationExecutor(id) != null) {
            getJpm().getAsynchronicOperationExecutor(id).addObserver(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            if (o instanceof AsynchronicOperationExecutor) {
                final Boolean ended = (Boolean) arg;
                final AsynchronicOperationExecutor t = (AsynchronicOperationExecutor) o;
                if (t.getId().contains(",")) {
                    for (String id : t.getId().split(",")) {
                        template.convertAndSend("/asynchronicOperationExecutor/" + (ended ? "done" : "progress") + "/" + id, getJpm().getAsynchronicOperationExecutor(id).getProgress());
                    }
                } else {
                    template.convertAndSend("/asynchronicOperationExecutor/" + (ended ? "done" : "progress") + "/" + t.getId(), getJpm().getAsynchronicOperationExecutor(t.getId()).getProgress());
                }
            }
        } catch (Exception e) {
            JPMUtils.getLogger().error("Error in ExecutorsController.update", e);
        }
    }

    protected OperationExecutor getExecutor() {
        return getContext().getOperation().getExecutor();
    }

    public static class MsgId {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return getId();
        }
    }
}
