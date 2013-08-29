package jpaoletti.jpm2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.NotAuthorizedException;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.core.model.SessionEntityData;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
public class BaseController {

    @Autowired
    public HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JPMContext context;
    //Messages TO-DO
    private List<Message> globalMessages = new ArrayList<>();
    private List<Message> entityMessages = new ArrayList<>();

    protected ModelAndView newMav() throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-" + getContext().getOperation().getId());
        mav.addObject("entity", getContext().getEntity());
        mav.addObject("operation", getContext().getOperation());
        if (getContext().getObject() != null) {
            mav.addObject("object", getContext().getObject());
        }
        mav.addObject("generalOperations", getContext().getEntity().getOperationsFor(null, getContext().getOperation(), OperationScope.GENERAL));
        mav.addObject("selectedOperations", getContext().getEntity().getOperationsFor(null, getContext().getOperation(), OperationScope.SELECTED));
        if (getContext().getObject() != null) {
            mav.addObject("itemOperations", getContext().getEntity().getOperationsFor(getContext().getObject(), getContext().getOperation(), OperationScope.ITEM));
        }
        if (getContext().getEntityInstance() != null) {
            mav.addObject("instance", getContext().getEntityInstance());
        }
        return mav;
    }

    public UserDetails getUserDetails() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        } else {
            return null;
        }
    }

    /**
     * Retrieves a bean from Spring context.
     *
     * @param name Bean name
     * @return bean
     */
    public static Object getBean(String name) {
        final WebApplicationContext context =
                WebApplicationContextUtils.getRequiredWebApplicationContext(
                ServletActionContext.getServletContext());
        return context.getBean(name);
    }

    public String getMessage(String code, String... args) {
        final Locale locale = getLocale();
        final WebApplicationContext ctx =
                WebApplicationContextUtils.getRequiredWebApplicationContext(
                ServletActionContext.getServletContext());
        return ctx.getMessage(code, args, locale);
    }

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public SessionEntityData getSessionEntityData(Entity entity) {
        final Object sed = getSession().getAttribute(entity.getId());
        if (sed == null) {
            final SessionEntityData sessionEntityData = new SessionEntityData(entity.getId());
            getSession().setAttribute(entity.getId(), sessionEntityData);
        }
        return (SessionEntityData) getSession().getAttribute(entity.getId());
    }

    protected EntityInstance newEntityInstance(String instanceId, Entity entity) throws PMException {
        return new EntityInstance(instanceId, entity, getContext().getOperation(), getContext().getObject());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleTypeMismatchException(MissingServletRequestParameterException ex, HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("Parameter failure: " + ex.getRootCause().getLocalizedMessage());
        System.out.println("Invalid name is: " + ex.getParameterName());
        System.out.println("Required type is: " + ex.getParameterType());
        return null;
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ModelAndView handleTypeMismatchException(TypeMismatchException ex, HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("Parameter failure: " + ex.getRootCause().getLocalizedMessage());
        System.out.println("Invalid value is: " + ex.getValue());
        System.out.println("Required type is: " + ex.getRequiredType().getSimpleName());
        return null;
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public String handleNotAuthorizedException() {
        return "not-authotized";
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

    public List<Message> getEntityMessages() {
        return entityMessages;
    }

    public void setEntityMessages(List<Message> entityMessages) {
        this.entityMessages = entityMessages;
    }
}
