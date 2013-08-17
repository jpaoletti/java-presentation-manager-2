package jpaoletti.jpm2.controller;

import com.opensymphony.xwork2.ActionContext;
import jpaoletti.jpm2.core.PresentationManager;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Base action for all jPM2 controller classes.
 *
 * @author jpaoletti
 */
public class BaseAction extends DefaultActionSupport {

    public static final String UNDEFINED_ENTITY_PARAMETER = "Undefined Entity Parameter";
    public static final String UNDEFINED_ENTITY = "Undefined Entity";
    public static final String UNDEFINED_OPERATION = "Undefined Operation";

    public ActionContext getActionContext() {
        return ActionContext.getContext();
    }

    public String getActionName() {
        return (String) getActionContext().get(ActionContext.ACTION_NAME);
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

    /**
     * Retrieves JPM bean.
     *
     * @return jpm
     */
    public PresentationManager getPm() {
        return (PresentationManager) getBean("jpm");
    }
}
