package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.entityparam.EntityParameterTree;
import jpaoletti.jpm2.core.entityparam.ParameterizedEntity;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityContext;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.controller.ListController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Field converter that renders a {@link ParameterizedEntity}'s parameters as a grouped tree with an inline
 * value editor, embedded directly in the parent's show (no separate operation — the sysparam-tree experience
 * inline). It returns a {@code @page:} fragment (like {@code WeakConverter}) so the fragment's scripts run;
 * the fragment builds the tree JSON from the {@code instance} in scope and posts each value change to the
 * child entity's {@code setEntityParameterValue} executor via AJAX.
 *
 * <p>Like {@code WeakConverter} it also renders a button to the child entity's weak list (auth-gated by the
 * list operation), so parameters can be <b>added/removed manually</b> there — the tree edits existing values
 * but cannot create rows (a new parameter, e.g. a dynamic-key one, is added from the list).
 *
 * @author jpaoletti
 */
public class EntityParamsTreeConverter extends Converter {

    /** Request attribute prefix under which the pre-built tree JSON is stashed (keyed by field id). */
    public static final String TREE_JSON_ATTR = "epTreeJson_";

    private Entity entity;
    private boolean reveal = false;
    private String btnText = "btn.parameters";
    private String btnIcon = "fas fa-th-list";
    private String context;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId)
            throws ConverterException, ConfigurationException {
        // The JSP fragment only sees the JPM instance wrapper, not the domain object; build the tree here (we
        // have the real ParameterizedEntity) and hand it to the fragment through a request attribute.
        final String json = (object instanceof ParameterizedEntity)
                ? EntityParameterTree.json((ParameterizedEntity<?>) object) : "[]";
        final RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            attrs.setAttribute(TREE_JSON_ATTR + field.getId(), json, RequestAttributes.SCOPE_REQUEST);
        }
        final StringBuilder res = new StringBuilder("@page:entity-params-tree.jsp");
        res.append("?childEntity=").append(entity.getId());
        res.append("&reveal=").append(reveal);
        res.append("&ownerId=").append(instanceId);
        res.append("&btnText=").append(btnText);
        res.append("&btnIcon=").append(btnIcon);
        final EntityContext weakContext = entity.getContext(context);
        try {
            res.append("&weakAuth=").append(entity.getOperation(ListController.OP_LIST, weakContext).getAuthKey(entity, weakContext));
        } catch (NotAuthorizedException ex) {
            res.append("&weakAuth=").append(ex.getMsg().getText());
        } catch (Exception ex) {
            JPMUtils.getLogger().error("Error in entity params tree converter: " + entity, ex);
            throw new ConverterException("unexpected.exception");
        }
        if (context != null) {
            res.append("&context=!").append(context);
        } else {
            res.append("&context=");
        }
        return res.toString();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean isReveal() {
        return reveal;
    }

    public void setReveal(boolean reveal) {
        this.reveal = reveal;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBtnIcon() {
        return btnIcon;
    }

    public void setBtnIcon(String btnIcon) {
        this.btnIcon = btnIcon;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
