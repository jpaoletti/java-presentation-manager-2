package jpaoletti.jpm2.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.AsynchronicOperationExecutor;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.OperationExecutor;
import static jpaoletti.jpm2.core.model.OperationExecutor.OWNER_ENTITY;
import static jpaoletti.jpm2.core.model.OperationExecutor.OWNER_ID;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.JPMAskConfirmationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    public static final String HTTP_SERVLET_REQUEST = "HTTP_SERVLET_REQUEST";
    public static final String LAST_ACCESSED = "LAST_ACCESSED_";

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * True when the request is a JPM partial (AJAX) navigation. Such requests
     * cannot follow a server 302 reliably (a cross-origin/scheme redirect makes
     * the XHR fail and the client falls back to a full-page GET that re-runs the
     * operation), so immediate operations answer with a JPMPostResponse instead.
     */
    private boolean isPartialNavigation(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "partial".equalsIgnoreCase(request.getHeader("X-JPM-NAVIGATION"));
    }

    /**
     * Resolves the URL the client must navigate to after an immediate operation,
     * preferring the executor's explicit result and falling back to the operation
     * "follows"/default next.
     */
    private String resolveNextUrl(JPMPostResponse response, String instanceIds) throws PMException {
        if (StringUtils.isNotEmpty(response.getNext())) {
            return response.getNext();
        }
        final String viewName = next(getContext().getEntity(), getContext().getOperation(), instanceIds, getExecutor().getDefaultNextOperationId()).getViewName();
        return viewName == null ? null : viewName.replaceFirst("^redirect:", "");
    }

    /**
     * Writes the given response as JSON and returns a null ModelAndView so Spring
     * treats the response as already handled.
     */
    private ModelAndView writeJsonResponse(HttpServletResponse httpResponse, JPMPostResponse body) throws PMException {
        try {
            httpResponse.setContentType("application/json;charset=UTF-8");
            JSON_MAPPER.writeValue(httpResponse.getWriter(), body);
            httpResponse.flushBuffer();
            return null;
        } catch (IOException e) {
            throw new PMException(e);
        }
    }

    /**
     * GET method prepares form.
     *
     * @param request
     * @return model and view
     * @throws PMException
     */
    @GetMapping(value = {"/jpm/{entity}/{operationId}.exec"})
    public ModelAndView executorsGeneralPrepare(HttpServletRequest request, HttpServletResponse httpResponse) throws PMException {
        return executorsGeneralPrepare(request, httpResponse, null, null);
    }

    @GetMapping(value = {"/jpm/{owner}/{ownerId}/{entity}/{operationId}.exec"})
    public ModelAndView executorsGeneralPrepare(HttpServletRequest request, HttpServletResponse httpResponse, @PathVariable Entity owner, @PathVariable String ownerId) throws PMException {
        final Map<String, Object> preparation = getExecutor().prepare(owner, ownerId, new ArrayList<>());
        if (getExecutor().immediateExecute() || preparation == null) {
            final JPMPostResponse response = executorsCommit(request, new ArrayList<>(), false);
            final Message message = response.getMessages().isEmpty()
                    ? MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success")
                    : response.getMessages().get(0);
            // For partial (AJAX) navigation, answer with a JPMPostResponse (JSON)
            // carrying the destination and the (localized) message instead of a
            // redirect: the client navigates there directly (avoiding a full-page
            // redirect that would re-run the immediate operation) and shows the
            // message as a flyover.
            if (isPartialNavigation(request)) {
                if (response.isOk()) {
                    final String nextUrl = resolveNextUrl(response, "");
                    return writeJsonResponse(httpResponse, new JPMPostResponse(true, nextUrl, message));
                }
                return writeJsonResponse(httpResponse, response);
            }
            getContext().setGlobalMessage(message);
            return next(getContext().getEntity(), getContext().getOperation(), "", getExecutor().getDefaultNextOperationId());
        } else {
//            final ModelAndView mav = new ModelAndView("op-" + getContext().getOperation().getId());
            final ModelAndView mav = new ModelAndView(getExecutor().getViewName(getContext().getOperation().getId()));
            preparation.entrySet().stream().forEach(
                    e -> mav.addObject(e.getKey(), e.getValue())
            );
            getRequest().getParameterMap().keySet().stream().forEach(
                    key -> mav.addObject((String) key, (String[]) getRequest().getParameterValues((String) key))
            );
            return mav;
        }
    }

    @PostMapping(value = {"/jpm/{owner}/{ownerId}/{entity}/{operationId}.exec"})
    @ResponseBody
    public JPMPostResponse executorsGeneralCommit(
            HttpServletRequest request,
            @PathVariable Entity owner, @PathVariable String ownerId,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final List<EntityInstance> instances = new ArrayList<>();
        final Map parameterMap = new LinkedHashMap(request.getParameterMap());
        parameterMap.put(HTTP_SERVLET_REQUEST, request);
        parameterMap.put(OWNER_ENTITY, owner);
        parameterMap.put(OWNER_ID, ownerId);
        final Map parameters = getExecutor().preExecute(getContext(), instances, parameterMap);
        return executorsGeneralCommit(instances, parameters, repeat);
    }

    @PostMapping(value = {"/jpm/{entity}/{operationId}.exec"})
    @ResponseBody
    public JPMPostResponse executorsGeneralCommit(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        final List<EntityInstance> instances = new ArrayList<>();
        final Map parameterMap = new LinkedHashMap(request.getParameterMap());
        parameterMap.put(HTTP_SERVLET_REQUEST, request);
        final Map parameters = getExecutor().preExecute(getContext(), instances, parameterMap);
        return executorsGeneralCommit(instances, parameters, repeat);
    }

    protected JPMPostResponse executorsGeneralCommit(final List<EntityInstance> instances, final Map parameters, boolean repeat) {
        try {
            String execute = null;
            if (getContext().getOperation().isSynchronic()) {
                execute = getExecutor().execute(getContext(), instances, parameters, new Progress());
            } else if (!getJpm().registerAsynchronicExecutor(getContext(), getExecutor(), instances, parameters, this)) {
                throw new PMException("unable.to.register.asynchronic.executor");
            }
            String buildRedirect;
            if (StringUtils.isEmpty(execute)) {
                if (repeat) {
                    buildRedirect = buildRedirect((Entity) parameters.get(OWNER_ENTITY), (String) parameters.get(OWNER_ID), getContext().getEntity(), null, getContext().getOperation().getPathId(), "repeated=true");
                } else {
                    if (getContext().getEntityInstance() != null && getContext().getEntityInstance().getIobject() != null) {
                        return new JPMPostResponse(true, next(getContext().getEntity(), getContext().getOperation(), getContext().getEntityInstance().getIobject().getId(), getExecutor().getDefaultNextOperationId()).getViewName());
                    }
                    buildRedirect = next(getContext().getEntity(), getContext().getOperation(), (Entity) parameters.get(OWNER_ENTITY), (String) parameters.get(OWNER_ID), getExecutor().getDefaultNextOperationId()).getViewName();
                }
            } else {
                buildRedirect = execute;
            }
            if (buildRedirect != null && buildRedirect.startsWith("message:")) {
                return new JPMPostResponse(true, null, MessageFactory.success(buildRedirect.replaceFirst("message:", "")));
            } else {
                return new JPMPostResponse(true, buildRedirect, MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success"));
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
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
        } catch (Exception e) {
            e.printStackTrace();
            getContext().getEntityMessages().add(MessageFactory.error(e.getMessage()));
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        }
    }

    /**
     * GET method prepares form.
     *
     * @param request
     * @param instanceIds
     * @return model and view
     * @throws PMException
     */
    @GetMapping(value = "/jpm/{entity}/{instanceIds}/{operationId}.exec")
    public ModelAndView executorsPrepare(HttpServletRequest request, HttpServletResponse httpResponse, @PathVariable List<String> instanceIds) throws PMException {
        final List<EntityInstance> instances = new ArrayList<>();
        for (String instanceId : instanceIds) {
            initItemControllerOperation(instanceId);
            instances.add(getContext().getEntityInstance());
        }
        setLastAccessed(request, getContext(), instanceIds);
        final Map<String, Object> preparation = getExecutor().prepare(null, null, instances);
        if (getExecutor().immediateExecute() || preparation == null) {
            final JPMPostResponse response = executorsCommit(request, instanceIds, false);
            if (response.isOk()) {
                final Message message = response.getMessages().isEmpty()
                        ? MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success")
                        : response.getMessages().get(0);
                final String nextUrl = resolveNextUrl(response, StringUtils.join(instanceIds, ","));
                // For partial (AJAX) navigation, answer with a JPMPostResponse (JSON)
                // carrying the destination and the (localized) message instead of a
                // redirect: the client navigates there directly (avoiding a full-page
                // redirect that would re-run the immediate operation) and shows the
                // message as a flyover.
                if (isPartialNavigation(request)) {
                    return writeJsonResponse(httpResponse, new JPMPostResponse(true, nextUrl, message));
                }
                getContext().setGlobalMessage(message);
                return new ModelAndView("redirect:" + nextUrl);
            } else {
                if (isPartialNavigation(request)) {
                    return writeJsonResponse(httpResponse, response);
                }
                if (response.getMessages().isEmpty()) {
                    getContext().setGlobalMessage(MessageFactory.success("jpm." + getContext().getOperation().getId() + ".error"));
                } else {
                    getContext().setGlobalMessage(response.getMessages().get(0));
                }
                return new ModelAndView("redirect:" + request.getHeader("Referer"));
            }
        } else {
            //final ModelAndView mav = new ModelAndView("op-" + getContext().getOperation().getId());
            final ModelAndView mav = new ModelAndView(getExecutor().getViewName(getContext().getOperation().getId()));
            preparation.entrySet().stream().forEach(
                    e -> mav.addObject(e.getKey(), e.getValue())
            );
            getRequest().getParameterMap().keySet().stream().forEach(
                    key -> mav.addObject((String) key, (String[]) getRequest().getParameterValues((String) key))
            );
            return mav;
        }
    }

    @PostMapping(value = "/jpm/{entity}/{instanceIds}/{operationId}.exec")
    @ResponseBody
    public JPMPostResponse executorsCommit(HttpServletRequest request, @PathVariable List<String> instanceIds, @RequestParam(required = false, defaultValue = "false") boolean repeat) throws PMException {
        try {
            final List<EntityInstance> instances = new ArrayList<>();
            for (String instanceId : instanceIds) {
                initItemControllerOperation(instanceId);
                instances.add(getContext().getEntityInstance());
            }
            setLastAccessed(request, getContext(), instanceIds);
            final Map parameterMap = new LinkedHashMap(request.getParameterMap());
            parameterMap.put(HTTP_SERVLET_REQUEST, request);
            final Map parameters = getExecutor().preExecute(getContext(), instances, parameterMap);
            String execute = null;
            if (getContext().getOperation().isSynchronic()) {
                execute = getExecutor().execute(getContext(), instances, parameters, new Progress());
            } else if (!getJpm().registerAsynchronicExecutor(getContext(), getExecutor(), instances, parameters, this)) {
                throw new PMException("unable.to.register.asynchronic.executor");
            }
            String buildRedirect;
            if (StringUtils.isEmpty(execute)) {
                final String newIds = StringUtils.join(instances.stream().map(EntityInstance::getId).collect(Collectors.toList()), ",");
                if (repeat) {
                    buildRedirect = buildRedirect(null, null, getContext().getEntity(), newIds, getContext().getOperation().getPathId(), "repeated=true");
                } else {
                    //BUG follows list in weak not working
                    buildRedirect = next(getContext().getEntity(), getContext().getOperation(), newIds, getExecutor().getDefaultNextOperationId()).getViewName();
                }
            } else {
                buildRedirect = execute;
            }
            if (buildRedirect != null && buildRedirect.startsWith("message:")) {
                return new JPMPostResponse(true, null, MessageFactory.success(buildRedirect.replaceFirst("message:", "")));
            } else {
                return new JPMPostResponse(true, buildRedirect, MessageFactory.success("jpm." + getContext().getOperation().getId() + ".success"));
            }
        } catch (ValidationException e) {
            if (e.getMsg() != null) {
                getContext().getEntityMessages().add(e.getMsg());
            }
            final IdentifiedObject iobject = new IdentifiedObject(StringUtils.join(instanceIds, ","), e.getValidatedObject());
            getContext().setEntityInstance(new EntityInstance(iobject, getContext()));
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
            } else {
                JPMUtils.getLogger().error(e);
            }
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        } catch (Exception e) {
            JPMUtils.getLogger().error("Error", e);
            getContext().getEntityMessages().add(MessageFactory.error(e.getMessage()));
            return new JPMPostResponse(false, null, getContext().getEntityMessages(), getContext().getFieldMessages());
        }
    }

    protected void setLastAccessed(HttpServletRequest request, JPMContext ctx, List<String> instanceIds) {
        request.getSession().setAttribute(LAST_ACCESSED + ctx.getContextualEntity().toString(), StringUtils.join(instanceIds, "|"));
    }

    public static String getLastAccessed(HttpServletRequest request, String contextualEntity) {
        return (String) request.getSession().getAttribute(LAST_ACCESSED + contextualEntity);
    }

    @MessageMapping("/asynchronicOperationExecutorProgress")
    public void asynchronicOperationExecutorProgress(MsgId message) throws Exception {
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
                        final AsynchronicOperationExecutor asynchronicOperationExecutor = getJpm().getAsynchronicOperationExecutor(id);
                        if (asynchronicOperationExecutor != null) {
                            template.convertAndSend("/asynchronicOperationExecutor/" + (ended ? "done" : "progress") + "/" + id,
                                    asynchronicOperationExecutor.getProgress()
                            );
                        }
                    }
                } else {
                    final AsynchronicOperationExecutor asynchronicOperationExecutor = getJpm().getAsynchronicOperationExecutor(t.getId());
                    if (asynchronicOperationExecutor != null) {
                        template.convertAndSend("/asynchronicOperationExecutor/" + (ended ? "done" : "progress") + "/" + t.getId(),
                                asynchronicOperationExecutor.getProgress()
                        );
                    }
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
