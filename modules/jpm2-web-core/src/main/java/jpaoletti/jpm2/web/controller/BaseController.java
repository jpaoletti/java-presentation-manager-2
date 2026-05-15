package jpaoletti.jpm2.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.exception.OperationNotFoundException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.ListSort;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.SearchDefinition;
import jpaoletti.jpm2.core.model.SessionEntityData;
import jpaoletti.jpm2.core.service.AuthorizationService;
import jpaoletti.jpm2.core.service.JPMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
public class BaseController {

    public static final String CURRENT_HOME = "currentHome";
    protected static final String LIST_STATE_PARAM = "_ls";
    protected static final String SORT_FIELD_PARAM = "_sf";
    protected static final String SORT_DIRECTION_PARAM = "_sd";
    protected static final String FILTER_PARAM_PREFIX = "_f.";
    protected static final String FILTER_FIELD_SUFFIX = ".fieldId";
    protected static final String FILTER_PARAMETER_PREFIX = ".p.";

    @Autowired
    public HttpSession session;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JPMContext context;
    @Autowired
    private PresentationManager jpm;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private MessageSource messageSource;
    //Messages TO-DO
    private List<Message> globalMessages = new ArrayList<>();

    public UserDetails getUserDetails() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        } else {
            return null;
        }
    }

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public SessionEntityData getSessionEntityData(Entity entity) throws PMException {
        final String key = "jpm_sed_" + entity.getId();
        final Object sed = getSession().getAttribute(key);
        if (sed == null) {
            final SessionEntityData sessionEntityData = new SessionEntityData(entity, getContext().getEntityContext());
            getSession().setAttribute(key, sessionEntityData);
        }
        return (SessionEntityData) getSession().getAttribute(key);
    }

    public void setSessionEntityData(Entity entity, SessionEntityData sessionEntityData) {
        final String key = "jpm_sed_" + entity.getId();
        getSession().setAttribute(key, sessionEntityData);
    }

    public void setCurrentHome(String newHome) {
        getSession().setAttribute(CURRENT_HOME, newHome);
    }

    /**
     * Redirect to list depending on weaks entities.
     *
     * @param instance
     * @param entity
     * @return
     * @throws jpaoletti.jpm2.core.exception.NotAuthorizedException
     */
    protected String toList(final EntityInstance instance, Entity entity) throws NotAuthorizedException {
        if (instance != null && instance.getOwner() != null) {
            return buildRedirect(instance.getOwner().getEntity(), instance.getOwnerId(), entity, null, ListController.OP_LIST, null);
        } else {
            return buildRedirect(entity, null, ListController.OP_LIST, null);
        }
    }

    protected ModelAndView next(Entity entity, Operation operation, final String instanceId, String defaultOp) throws OperationNotFoundException, NotAuthorizedException {
        final String nextOpId = (operation == null || operation.getFollows() == null) ? defaultOp : operation.getFollows();
        final Operation nextOp = entity.getOperation(nextOpId, getContext().getContext());
        final EntityInstance instance = getContext().getEntityInstance();
        final String nexOpPath = nextOp.getPathId();
        if (OperationScope.ITEM.equals(nextOp.getScope())) {
            return new ModelAndView(buildRedirect(entity, instanceId, nexOpPath, null));
        } else if (instance != null && instance.getOwner() != null) {
            return new ModelAndView(buildRedirect(instance.getOwner().getEntity(), instance.getOwnerId(), entity, null, nexOpPath, null));
        } else {
            return new ModelAndView(buildRedirect(entity, null, nexOpPath, null));
        }
    }

    protected ModelAndView next(Entity entity, Operation operation, Entity owner, String ownerId, String defaultOp) throws OperationNotFoundException, NotAuthorizedException {
        final String nextOpId = (operation.getFollows() == null) ? defaultOp : operation.getFollows();
        final Operation nextOp = entity.getOperation(nextOpId, getContext().getContext());
        final EntityInstance instance = getContext().getEntityInstance();
        final String nexOpPath = nextOp.getPathId();
        switch (nextOp.getScope()) {
            case ITEM:
                return new ModelAndView(buildRedirect(owner, ownerId, entity, instance == null ? null : instance.getId(), nexOpPath, null));
            default:
                return new ModelAndView(buildRedirect(owner, ownerId, entity, null, nexOpPath, null));
        }
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public Map getParameterMap() {
        return getRequest().getParameterMap();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    public List<Message> getGlobalMessages() {
        return globalMessages;
    }

    public void setGlobalMessages(List<Message> globalMessages) {
        this.globalMessages = globalMessages;
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected JPMService getService() {
        return getJpm().getService();
    }

    protected String getInternationalizedMessage(Message msg) {
        return getMessageSource().getMessage(msg.getKey(), msg.getArgs(), getLocale());
    }

    protected void checkOperationCondition(Operation operation, EntityInstance instance) throws PMException {
        getContext().getContextualEntity().checkAuthorization();
        operation.checkAuthorization();
        if (operation.getCondition() != null) {
            if (!operation.getCondition().check(instance, operation, null)) {
                throw new NotAuthorizedException();
            }
        }
    }

    protected IdentifiedObject initItemControllerOperation(String instanceId) throws PMException {
        final Operation operation = getContext().getOperation();
        final IdentifiedObject iobject = getJpm().getService().get(getContext().getEntity(), getContext().getEntityContext(), operation, instanceId);
        if (operation.getContext() != null) {
            operation.getContext().preConversion(iobject.getObject());
        }
        getContext().setEntityInstance(new EntityInstance(iobject, getContext()));
        checkOperationCondition(getContext().getOperation(), getContext().getEntityInstance());
        return iobject;
    }

    public String buildRedirect(Entity entity, String instanceId, String operation, String parameters) {
        return buildRedirect(null, null, entity, instanceId, operation, parameters);
    }

    public String buildRedirect(Entity owner, String ownerId, Entity entity, String instanceId, String operation, String parameters) {
        final StringBuilder sb = new StringBuilder("redirect:/jpm");
        if (owner != null) {
            sb.append("/").append(owner.getId());
            if (getContext().getEntityContext() != null) {
                sb.append(PresentationManager.CONTEXT_SEPARATOR).append(getContext().getEntityContext());
            }
            sb.append("/").append(ownerId);
        }
        sb.append("/").append(entity.getId());
        if (getContext().getEntityContext() != null) {
            sb.append(PresentationManager.CONTEXT_SEPARATOR).append(getContext().getEntityContext());
        }
        if (instanceId != null) {
            sb.append("/").append(instanceId);
        }
        sb.append("/").append(operation);
        if (parameters != null) {
            sb.append("?").append(parameters);
        }
        return sb.toString();
    }

    protected void clearSessionEntitySearchDefinitions(Entity entity) throws PMException {
        getSessionEntityData(entity).clearSearchDefinitions();
    }

    protected boolean addSessionEntitySearchDefinition(Entity entity, String fieldId, Map<String, String[]> parameterMap) throws PMException {
        return getSessionEntityData(entity).addSearchDefinition(buildSearchDefinition(fieldId, parameterMap), getContext().getEntityContext());
    }

    protected String buildCanonicalListRedirect(Entity entity, Integer pageOverride) throws PMException {
        return buildCanonicalListRedirect(null, null, entity, pageOverride);
    }

    protected String buildCanonicalListRedirect(Entity owner, String ownerId, Entity entity, Integer pageOverride) throws PMException {
        final SessionEntityData sessionEntityData = getSessionEntityData(entity);
        final Integer page = pageOverride != null ? pageOverride : (sessionEntityData.getPage() == null ? 1 : sessionEntityData.getPage());
        final Integer pageSize = sessionEntityData.getPageSize();
        return buildRedirect(owner, ownerId, entity, null, ListController.OP_LIST, buildQueryString(buildListStateParameters(sessionEntityData, page, pageSize, true)));
    }

    protected Map<String, List<String>> buildListStateHiddenParameters(SessionEntityData sessionEntityData) {
        return buildListStateParameters(sessionEntityData, null, null, true);
    }

    protected Map<String, List<String>> buildListStateParameters(SessionEntityData sessionEntityData, Integer page, Integer pageSize, boolean includeListStateMarker) {
        final Map<String, List<String>> parameters = new LinkedHashMap<>();
        if (page != null) {
            addParameter(parameters, "page", String.valueOf(page));
        }
        if (pageSize != null) {
            addParameter(parameters, "pageSize", String.valueOf(pageSize));
        }
        if (includeListStateMarker) {
            addParameter(parameters, LIST_STATE_PARAM, "1");
        }
        if (sessionEntityData.getSort() != null && sessionEntityData.getSort().isSorted()) {
            addParameter(parameters, SORT_FIELD_PARAM, sessionEntityData.getSort().getField().getId());
            addParameter(parameters, SORT_DIRECTION_PARAM, sessionEntityData.getSort().getDirection().name());
        }
        int index = 0;
        for (SearchDefinition definition : sessionEntityData.getSearchDefinitions()) {
            addParameter(parameters, FILTER_PARAM_PREFIX + index + FILTER_FIELD_SUFFIX, definition.getFieldId());
            if (definition.getParameters() != null) {
                for (Map.Entry<String, List<String>> entry : definition.getParameters().entrySet()) {
                    if (entry.getValue() == null || entry.getValue().isEmpty()) {
                        addParameter(parameters, FILTER_PARAM_PREFIX + index + FILTER_PARAMETER_PREFIX + entry.getKey(), "");
                    } else {
                        for (String value : entry.getValue()) {
                            addParameter(parameters, FILTER_PARAM_PREFIX + index + FILTER_PARAMETER_PREFIX + entry.getKey(), value);
                        }
                    }
                }
            }
            index++;
        }
        return parameters;
    }

    protected SearchDefinition buildSearchDefinition(String fieldId, Map<String, String[]> parameterMap) {
        final Map<String, List<String>> parameters = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (!isSearchParameter(entry.getKey())) {
                continue;
            }
            final List<String> values = new ArrayList<>();
            if (entry.getValue() != null) {
                Collections.addAll(values, entry.getValue());
            }
            parameters.put(entry.getKey(), values);
        }
        return new SearchDefinition(fieldId, parameters);
    }

    protected boolean isSearchParameter(String parameterName) {
        return !"fieldId".equals(parameterName)
                && !"entityId".equals(parameterName)
                && !"_csrf".equals(parameterName)
                && !parameterName.startsWith("_");
    }

    protected void addParameter(Map<String, List<String>> parameters, String name, String value) {
        parameters.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
    }

    protected String buildQueryString(Map<String, List<String>> parameters) {
        final List<String> parts = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                parts.add(urlEncode(entry.getKey()) + "=");
                continue;
            }
            for (String value : entry.getValue()) {
                parts.add(urlEncode(entry.getKey()) + "=" + urlEncode(value == null ? "" : value));
            }
        }
        return String.join("&", parts);
    }

    protected String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
