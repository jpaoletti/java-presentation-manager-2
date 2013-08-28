package jpaoletti.jpm2.core;

import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;

/**
 * Context for jpm operations.
 *
 * @author jpaoletti
 */
public interface JPMContext {

    /**
     * Current entity.
     */
    public Entity getEntity();

    public void setEntity(Entity entity);

    /**
     * Current operation.
     */
    public Operation getOperation();

    public void setOperation(Operation operation);

    /**
     * Current selected object. May be null.
     */
    public Object getObject();

    public void setObject(Object object);
}
