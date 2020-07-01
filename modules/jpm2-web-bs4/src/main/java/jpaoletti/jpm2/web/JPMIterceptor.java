package jpaoletti.jpm2.web;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
public class JPMIterceptor implements HandlerInterceptor {

    @Autowired
    private JPMContext context;
    @Autowired
    private PresentationManager jpm;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse hsr1, Object o) throws Exception {
        final Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null && !pathVariables.isEmpty()) {
            if (pathVariables.containsKey("entity")) {
                final String entityId = (String) pathVariables.get("entity");
                final ContextualEntity contextualEntity = getJpm().getContextualEntity(entityId);
                final Entity entity = contextualEntity.getEntity();
                getContext().setEntityContext(contextualEntity.getContext());
                if (pathVariables.containsKey("operationId")) {
                    final Operation operation = entity.getOperation((String) pathVariables.get("operationId"), entity.getContext(contextualEntity.getContext()));
                    getContext().set(entity, operation);
                } else {
                    getContext().setEntity(entity);
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest hsr, HttpServletResponse hsr1, Object o, ModelAndView mav) throws Exception {
        try {
            mav.addObject("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } catch (Exception e) {
        }
        final JPMContext ctx = getContext();
        if (ctx != null) {
            final Entity entity = ctx.getEntity();
            final Operation operation = ctx.getOperation();
            if (mav != null) {
                mav.addObject("locale", LocaleContextHolder.getLocale());
                if (entity != null) {
                    mav.addObject("entity", entity);
                    mav.addObject("contextualEntity", ctx.getContextualEntity());
                    final EntityInstance instance = ctx.getEntityInstance();
                    mav.addObject("instance", instance);
                    if (operation != null) {
                        mav.addObject("operation", operation);
                        mav.addObject("generalOperations", entity.getOperationsFor(instance, ctx.getEntityContext(), operation, OperationScope.GENERAL));
                        mav.addObject("selectedOperations", entity.getOperationsFor(instance, ctx.getEntityContext(), operation, OperationScope.SELECTED, OperationScope.GROUPED));
                    }
                    if (instance != null) {
                        if (instance.getIobject() != null) {
                            final Object object = instance.getIobject().getObject();
                            if (object != null) {
                                mav.addObject("object", object);
                                if (operation != null) {
                                    mav.addObject("itemOperations", entity.getOperationsFor(instance, ctx.getEntityContext(), operation, OperationScope.ITEM));
                                }
                            }
                        }
                        if (entity.isWeak(ctx.getEntityContext()) && instance.getOwner() != null) {
                            mav.addObject("owner", instance.getOwner().getEntity());
                            mav.addObject("ownerId", instance.getOwnerId());
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
            try {
                //Ignores json request
                if (entity != null && ctx.getEntity().getHome() != null && !hsr.getRequestURL().toString().contains(".json")) {
                    hsr.getSession().setAttribute(BaseController.CURRENT_HOME, ctx.getEntity().getHome(ctx.getEntityContext()));
                }
                if (ctx.getGlobalMessage() != null && mav != null) {
                    hsr.getSession().setAttribute("globalMessage", ctx.getGlobalMessage());
                }
            } catch (Exception e) {
                //Some times session can't be created
            }
            if (ctx.getEntityContext() != null && mav != null) {
                mav.addObject("entityContext", PresentationManager.CONTEXT_SEPARATOR + ctx.getEntityContext());
            }
        }
        try {
            if (hsr.getSession().getAttribute(BaseController.CURRENT_HOME) == null) {
                hsr.getSession().setAttribute(BaseController.CURRENT_HOME, "index");
            }
        } catch (Exception e) {
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

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }
}
