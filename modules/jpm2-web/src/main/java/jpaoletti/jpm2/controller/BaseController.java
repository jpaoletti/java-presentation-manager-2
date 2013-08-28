package jpaoletti.jpm2.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.OperationController;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.SessionEntityData;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
public class BaseController implements OperationController {

    @Autowired
    public HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JPMContext context;

    public SessionEntityData getSessionEntityData(Entity entity) {
        final Object sed = getSession().getAttribute(entity.getId());
        if (sed == null) {
            final SessionEntityData sessionEntityData = new SessionEntityData(entity.getId());
            getSession().setAttribute(entity.getId(), sessionEntityData);
        }
        return (SessionEntityData) getSession().getAttribute(entity.getId());
    }

    public void preConversion() throws PMException {
        if (getContext().getOperation().getContext() != null) {
            getContext().getOperation().getContext().preConversion(this);
        }
    }

    public void preExecute() throws PMException {
        if (getContext().getOperation().getValidator() != null) {
            getContext().getOperation().getValidator().validate(getObject());
        }
        if (getContext().getOperation().getContext() != null) {
            getContext().getOperation().getContext().preExecute(this);
        }
    }

    public void postExecute() throws PMException {
        if (getContext().getOperation().getContext() != null) {
            getContext().getOperation().getContext().postExecute(this);
        }
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

    @Override
    public Entity getEntity() {
        return getContext().getEntity();
    }

    @Override
    public String getEntityId() {
        return getContext().getEntity().getId();
    }

    @Override
    public Object getObject() {
       return getContext().getObject();
    }

    @Override
    public List<Operation> getItemOperations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Operation> getGeneralOperations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Operation> getSelectedOperations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getParameter(String param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
