package jpaoletti.jpm2.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.controller.BaseController;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
public class JPMIterceptor implements HandlerInterceptor {

    @Autowired
    private JPMContext context;

    @Override
    public boolean preHandle(HttpServletRequest hsr, HttpServletResponse hsr1, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest hsr, HttpServletResponse hsr1, Object o, ModelAndView mav) throws Exception {
        final JPMContext ctx = getContext();
        if (ctx != null) {
            final Entity entity = ctx.getEntity();
            final Operation operation = ctx.getOperation();
            if (mav != null) {
                if (entity != null) {
                    mav.addObject("entity", entity);
                    if (operation != null) {
                        mav.addObject("operation", operation);
                        mav.addObject("generalOperations", entity.getOperationsFor(null, operation, OperationScope.GENERAL));
                        mav.addObject("selectedOperations", entity.getOperationsFor(null, operation, OperationScope.SELECTED));
                    }
                    final Object object = ctx.getObject();
                    if (object != null) {
                        mav.addObject("object", object);
                        if (operation != null) {
                            mav.addObject("itemOperations", entity.getOperationsFor(object, operation, OperationScope.ITEM));
                        }
                    }
                    if (ctx.getEntityInstance() != null) {
                        mav.addObject("instance", ctx.getEntityInstance());
                        if (entity.isWeak()) {
                            mav.addObject("owner", ctx.getEntityInstance().getOwner());
                            mav.addObject("ownerId", ctx.getEntityInstance().getOwnerId());
                        }
                    }
                    if (!ctx.getEntityMessages().isEmpty()) {
                        mav.addObject("entityMessages", ctx.getEntityMessages());
                    }
                    if (!ctx.getFieldMessages().isEmpty()) {
                        mav.addObject("fieldMessages", ctx.getFieldMessages());
                    }
                }
            }
            if (entity != null) {
                hsr.getSession().setAttribute(BaseController.CURRENT_HOME, ctx.getEntity().getHome());
            }
        }
        if (hsr.getSession().getAttribute(BaseController.CURRENT_HOME) == null) {
            hsr.getSession().setAttribute(BaseController.CURRENT_HOME, "index");
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest hsr, HttpServletResponse hsr1, Object o, Exception excptn) throws Exception {
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }
}
