package jpaoletti.jpm2.controller;

import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Field;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Base action for all jPM2 controller classes.
 *
 * @author jpaoletti
 */
public class BaseAction extends DefaultActionSupport implements SessionAware, ServletResponseAware, ServletRequestAware {

    public static final String UNDEFINED_ENTITY_PARAMETER = "Undefined Entity Parameter";
    public static final String UNDEFINED_ENTITY = "Undefined Entity";
    public static final String UNDEFINED_OPERATION = "Undefined Operation";
    public static final String UNDEFINED_OBJECT = "Undefined Object";
    //Messages
    private List<Message> globalMessages = new ArrayList<>();
    private List<Message> entityMessages = new ArrayList<>();
    private Map<String, List<Message>> fieldMessages = new HashMap<>(); //field
    //Struts and Http stuff
    private Map<String, Object> session;
    private HttpServletResponse servletResponse;
    private HttpServletRequest servletRequest;
    private HttpSession httpSession;

    public ActionContext getActionContext() {
        return ActionContext.getContext();
    }

    public String getActionName() {
        return (String) getActionContext().get(ActionContext.ACTION_NAME);
    }

    public UserDetails getUserDetails() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        } else {
            return null;
        }
    }

    protected void addFieldMsg(final Field field, Message message) {
        if (!getFieldMessages().containsKey(field.getId())) {
            getFieldMessages().put(field.getId(), new ArrayList<Message>());
        }
        getFieldMessages().get(field.getId()).add(message);
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
        final WebApplicationContext context =
                WebApplicationContextUtils.getRequiredWebApplicationContext(
                ServletActionContext.getServletContext());
        return context.getMessage(code, args, locale);
    }

    /**
     * Retrieves JPM bean.
     *
     * @return jpm
     */
    public PresentationManager getPm() {
        return (PresentationManager) getBean("jpm");
    }

    public List<String> getParameter(String param) {
        final Object value = getActionContext().getParameters().get(param);
        if (value == null) {
            return null;
        }
        if (value instanceof String[]) {
            return Arrays.asList((String[]) value);
        } else {
            return Arrays.asList((String) value);
        }
    }

    protected String getStringParameter(String string) {
        final List<String> list = getParameter(string);
        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<Message> getGlobalMessages() {
        return globalMessages;
    }

    public List<Message> getEntityMessages() {
        return entityMessages;
    }

    public Map<String, List<Message>> getFieldMessages() {
        return fieldMessages;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    @Override
    public void setServletResponse(HttpServletResponse hsr) {
        this.servletResponse = hsr;
    }

    @Override
    public void setServletRequest(HttpServletRequest hsr) {
        this.servletRequest = hsr;
        this.httpSession = hsr.getSession(true);
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public Cookie getCookieValue(String cookieName) {
        Cookie cookies[] = servletRequest.getCookies();
        Cookie myCookie = null;
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(cookieName)) {
                    myCookie = cookies[i];
                    break;
                }
            }
        }
        return myCookie;
    }

    public void setCookieValue(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(60 * 60 * 24 * 365); // Make the cookie last a year  
        servletResponse.addCookie(cookie);
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
