package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.exception.ConfigurationException;

/**
 *
 * @author jpaoletti
 */
public class EntityInstanceOwner {

    private Entity entity;
    private IdentifiedObject iobject;
    private EntityInstanceOwner owner; //more than 1 ownership level

    public EntityInstanceOwner(Entity entity, IdentifiedObject iobject) throws ConfigurationException {
        this.entity = entity;
        this.iobject = iobject;
        if (entity.isWeak() && iobject != null) {
            final Entity superOwnerEntity = entity.getOwner().getOwner();
            final Object superOwnerobject = entity.getOwner().getOwnerObject(null, iobject.getObject());
            final String superOwnerId = String.valueOf(superOwnerEntity.getDao().getId(superOwnerobject));
            this.owner = new EntityInstanceOwner(superOwnerEntity, new IdentifiedObject(superOwnerId, superOwnerobject));
        }
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

    public EntityInstanceOwner getOwner() {
        return owner;
    }

    public void setOwner(EntityInstanceOwner owner) {
        this.owner = owner;
    }
}
