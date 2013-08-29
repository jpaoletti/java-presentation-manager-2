package jpaoletti.jpm2.core;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
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

    public EntityInstance getEntityInstance();

    public void setEntityInstance(EntityInstance instance);

    public void addFieldMsg(final Field field, Message message);

    public Map<String, List<Message>> getFieldMessages();

    public List<Message> getEntityMessages();
}
