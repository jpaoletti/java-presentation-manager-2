package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.util.JPMUtils;

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
    //If true, the localProperty is not the owner object but it's id
    private boolean onlyId = false;
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

    /**
     * The owner of the given (weak) object: the owner instance or, when
     * onlyId is true, the owner id stored in the local property.
     */
    public Object getOwnerObject(String context, Object object) throws ConfigurationException {
        return JPMUtils.get(object, getLocalProperty());
    }

    /**
     * Sets the owner instance into the weak object. When onlyId is true, the
     * owner id is stored in the local property.
     */
    public void setOwnerObject(String context, Object object, Object ownerObject) {
        if (isOnlyId()) {
            JPMUtils.set(object, getLocalProperty(), ownerObject == null ? null : getOwner().getDao().getId(ownerObject));
        } else {
            JPMUtils.set(object, getLocalProperty(), ownerObject);
        }
    }

    public boolean isOnlyId() {
        return onlyId;
    }

    public void setOnlyId(boolean onlyId) {
        this.onlyId = onlyId;
    }

}
