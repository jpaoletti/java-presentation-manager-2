package jpaoletti.jpm2.core.model;

/**
 * A container for an object and its String version identifier.
 *
 * @author jpaoletti
 */
public class IdentifiedObject {

    private String id;
    private Object object;

    public IdentifiedObject(String id) {
        this.id = id;
        this.object = null;
    }

    public IdentifiedObject(String id, Object object) {
        this.id = id;
        this.object = object;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
