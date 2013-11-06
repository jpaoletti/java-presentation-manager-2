package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMCoreObject;

/**
 * EntityOwner is the representation of the Entity Owner in weak entities.
 *
 * @author jpaoletti
 * @see Entity#owner
 *
 */
public class EntityOwner extends PMCoreObject {

    //Owner entity
    private Entity owner;
    // The property of the local entity that points to the owner (optional)
    private String localProperty;
    private boolean optional = false;

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * @return the localProperty
     */
    public String getLocalProperty() {
        return localProperty;
    }

    /**
     * @param localProperty the localProperty to set
     */
    public void setLocalProperty(String localProperty) {
        this.localProperty = localProperty;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }
}
