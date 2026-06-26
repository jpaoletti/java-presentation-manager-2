package jpaoletti.jpm2.core.model.persistent;

import jakarta.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Objects;

/**
 * Base por persistent generic objects
 *
 * @author jpaoletti
 */
public abstract class JPMPersistentObject implements Serializable {

    public JPMPersistentObject() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!isValidClass(obj)) {
            return false;
        }
        final JPMPersistentObject other = (JPMPersistentObject) obj;
        return !(!Objects.equals(this.getId(), other.getId()) && (this.getId() == null || !this.getId().equals(other.getId())));
    }

    protected abstract boolean isValidClass(Object obj);

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;

    }

    @XmlElement
    public abstract Long getId();

    @XmlElement
    public String getIdStr() {
        if (getId() == null) {
            return "";
        }
        return Long.toString(getId());
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "...";
        } else {
            return getClass().getSimpleName() + " " + getId().toString();
        }
    }
}
