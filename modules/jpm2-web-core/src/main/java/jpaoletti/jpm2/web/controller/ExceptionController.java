package jpaoletti.jpm2.web.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.exception.ConditionNotMetException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Global exception handler.
 *
 * @author jpaoletti
 */
@ControllerAdvice
public class ExceptionController {

    @Autowired(required = false)
    private JPMContext context;

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleTypeMismatchException(MissingServletRequestParameterException ex, HttpServletRequest req, HttpServletResponse resp) {
        try {
            JPMUtils.getLogger().warn("Parameter failure: " + ex.getRootCause().getLocalizedMessage());
            JPMUtils.getLogger().warn("Invalid name is: " + ex.getParameterName());
            JPMUtils.getLogger().warn("Required type is: " + ex.getParameterType());
        } catch (Exception e) {
        }
        return null;
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ModelAndView handleTypeMismatchException(TypeMismatchException ex, HttpServletRequest req, HttpServletResponse resp) {
        JPMUtils.getLogger().warn("Parameter failure: " + ex.getRootCause().getLocalizedMessage());
        JPMUtils.getLogger().warn("Invalid value is: " + ex.getValue());
        JPMUtils.getLogger().warn("Required type is: " + ex.getRequiredType().getSimpleName());
        JPMUtils.getLogger().warn("Request URL: " + req.getRequestURL().toString());
        JPMUtils.getLogger().warn("Request Params: " + req.getQueryString());
        return null;
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ModelAndView handleUnsupportedOperationException(UnsupportedOperationException ex, HttpServletRequest req, HttpServletResponse resp) {
        final ModelAndView mav = new ModelAndView("exception");
        mav.addObject("message", MessageFactory.error("unexpected.exception", ex.getMessage()));
        JPMUtils.getLogger().fatal("Unsupported operation at " + req.getRequestURL() + (req.getQueryString() == null ? "" : "?" + req.getQueryString()), ex);
        return mav;
    }

    /**
     * The condition of an operation was not met. Unlike a plain authorization
     * failure, this one may carry a message explaining the reason.
     *
     * @param ex the exception
     * @return the access denied view, with the message when there is one
     */
    @ExceptionHandler(ConditionNotMetException.class)
    public ModelAndView handleConditionNotMetException(ConditionNotMetException ex) {
        final ModelAndView mav = notAuthorizedView(ex.getGoToOperation());
        mav.addObject("message", ex.getMsg()); //May be null, the view falls back to the generic text
        JPMUtils.getLogger().debug("Operation condition not met: " + ex);
        return mav;
    }

    /**
     * The interceptor does not reach postHandle when an exception is thrown, so
     * the denied page gets here what it needs to show the usual header and,
     * when there is one, the link to go somewhere else.
     *
     * @param goToOperationId id of the operation to link to, may be null
     * @return the access denied view with the current entity context
     */
    private ModelAndView notAuthorizedView(String goToOperationId) {
        final ModelAndView mav = new ModelAndView("not-authotized");
        mav.addObject("locale", LocaleContextHolder.getLocale());
        try {
            mav.addObject("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } catch (Exception ignoreme) {
        }
        if (context == null || context.getEntity() == null) {
            return mav;
        }
        final Entity entity = context.getEntity();
        final EntityInstance instance = context.getEntityInstance();
        mav.addObject("entity", entity);
        mav.addObject("contextualEntity", context.getContextualEntity());
        mav.addObject("operation", context.getOperation());
        mav.addObject("instance", instance);
        if (instance != null && instance.getIobject() != null) {
            mav.addObject("object", instance.getIobject().getObject());
        }
        if (goToOperationId != null) {
            try {
                final Operation goTo = entity.getOperation(goToOperationId, context.getContext());
                mav.addObject("goToOperation", goTo);
                if (OperationScope.ITEM.equals(goTo.getScope()) && instance != null) {
                    mav.addObject("goToInstanceId", instance.getId());
                }
            } catch (Exception e) {
                //Not found or not authorized: no link is offered at all
                JPMUtils.getLogger().debug("Cannot link to operation " + goToOperationId + ": " + e);
            }
        }
        return mav;
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ModelAndView handleNotAuthorizedException(NotAuthorizedException ex) {
        //No message here on purpose: the key of an authority is not meant to be read by the user
        JPMUtils.getLogger().warn("Not authorized: " + ex);
        return notAuthorizedView(null);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleTypeMismatchException(Exception ex, HttpServletRequest req, HttpServletResponse resp) {
        final ModelAndView mav = new ModelAndView("exception");
        mav.addObject("message", MessageFactory.error("unexpected.exception", ex.getMessage()));
        if (ex.getMessage() == null || !ex.getMessage().contains("Broken pipe")) {
            JPMUtils.getLogger().fatal("Unexpected Exception", ex);
        }
        return mav;
    }

    @ExceptionHandler(PMException.class)
    public ModelAndView handlePMException(PMException ex) {
        final ModelAndView mav = new ModelAndView("exception");
        mav.addObject("message", ex.getMsg());
        return mav;
    }

    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class, DataIntegrityViolationException.class, ConstraintViolationException.class})
    public ModelAndView handleSQLConstraintException(Exception ex) {
        final ModelAndView mav = new ModelAndView("exception");
        mav.addObject("message", MessageFactory.error("jpm.constraint.exception"));
        JPMUtils.getLogger().fatal("Constraint Exception", ex);
        return mav;
    }
}
