package jpaoletti.jpm2.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.TypeMismatchException;
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
        return null;
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public String handleUnsupportedOperationException(UnsupportedOperationException ex, HttpServletRequest req, HttpServletResponse resp) {
        return "not-implemented";
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public String handleNotAuthorizedException() {
        return "not-authotized";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleTypeMismatchException(Exception ex, HttpServletRequest req, HttpServletResponse resp) {
        final ModelAndView mav = new ModelAndView("exception");
        mav.addObject("message", MessageFactory.error("unexpected.exception", ex.getMessage()));
        JPMUtils.getLogger().fatal("Unexpected Exception", ex);
        return mav;
    }

    @ExceptionHandler(PMException.class)
    public ModelAndView handlePMException(PMException ex) {
        final ModelAndView mav = new ModelAndView("exception");
        mav.addObject("message", ex.getMsg());
        return mav;
    }
}
