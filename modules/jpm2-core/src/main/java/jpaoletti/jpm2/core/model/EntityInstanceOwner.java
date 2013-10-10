package jpaoletti.jpm2.core.model;

/**
 *
 * @author jpaoletti
 */
public class EntityInstanceOwner {

    private Entity entity;
    private IdentifiedObject iobject;

    public EntityInstanceOwner(Entity entity, IdentifiedObject iobject) {
        this.entity = entity;
        this.iobject = iobject;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public IdentifiedObject getIobject() {
        return iobject;
    }

    public void setIobject(IdentifiedObject iobject) {
        this.iobject = iobject;
    }
}
