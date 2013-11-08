package jpaoletti.jpm2.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.exception.OperationNotFoundException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.SessionEntityData;
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
    @Autowired
    public HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JPMContext context;
    @Autowired
    private PresentationManager jpm;
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
        final Object sed = getSession().getAttribute(entity.getId());
        if (sed == null) {
            final SessionEntityData sessionEntityData = new SessionEntityData(entity);
            getSession().setAttribute(entity.getId(), sessionEntityData);
        }
        return (SessionEntityData) getSession().getAttribute(entity.getId());
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
        if (instance.getOwner() != null) {
            return String.format("redirect:/jpm/%s/%s/%s/list", instance.getOwner().getEntity().getId(), instance.getOwnerId(), entity.getId());
        } else {
            return String.format("redirect:/jpm/%s/list", entity.getId());
        }
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
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

    protected ModelAndView next(Entity entity, Operation operation, final String instanceId, String defaultOp) throws OperationNotFoundException, NotAuthorizedException {
        final String nextOpId = (operation.getFollows() == null) ? defaultOp : operation.getFollows();
        final Operation nextOp = entity.getOperation(nextOpId);
        final EntityInstance instance = getContext().getEntityInstance();
        if (OperationScope.ITEM.equals(nextOp.getScope())) {
            return new ModelAndView(String.format("redirect:/jpm/%s/%s/%s", entity.getId(), instanceId, nextOp));
        } else {
            if (instance != null && instance.getOwner() != null) {
                return new ModelAndView(String.format("redirect:/jpm/%s/%s/%s/%s", instance.getOwner().getEntity().getId(), instance.getOwnerId(), entity.getId(), nextOp));
            } else {
                return new ModelAndView(String.format("redirect:/jpm/%s/%s", entity.getId(), nextOp));
            }
        }
    }

    protected JPMService getService() {
        return getJpm().getService();
    }

    protected String getInternationalizedMessage(Message msg) {
        return getMessageSource().getMessage(msg.getKey(), msg.getArgs(), getLocale());
    }
}
