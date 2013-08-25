package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.ERROR;
import static jpaoletti.jpm2.controller.BaseAction.UNDEFINED_ENTITY;
import static jpaoletti.jpm2.controller.BaseAction.UNDEFINED_ENTITY_PARAMETER;
import static jpaoletti.jpm2.controller.BaseAction.getBean;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.SessionEntityData;

/**
 *
 * @author jpaoletti
 */
public class EntityAction extends BaseAction {

    private String entityId;
    private Entity entity;

    public String loadEntity() {
        if (getEntityId() == null || getEntityId().trim().equals("")) {
            getActionErrors().add(UNDEFINED_ENTITY_PARAMETER);
            return ERROR;
        }
        entity = (Entity) getBean(getEntityId());
        if (entity == null) {
            getActionErrors().add(UNDEFINED_ENTITY);
            return ERROR;
        }
        return SUCCESS;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public SessionEntityData getSessionEntityData() {
        final Object sed = getHttpSession().getAttribute(getEntityId());
        if (sed == null) {
            final SessionEntityData sessionEntityData = new SessionEntityData(getEntityId());
            getHttpSession().setAttribute(getEntityId(), sessionEntityData);
        }
        return (SessionEntityData) getHttpSession().getAttribute(getEntityId());
    }
}
