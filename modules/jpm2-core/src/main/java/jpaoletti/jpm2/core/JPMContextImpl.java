package jpaoletti.jpm2.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class JPMContextImpl implements JPMContext {

    private Entity entity;
    private Operation operation;
    private EntityInstance entityInstance;
    private Map<String, List<Message>> fieldMessages; //field
    private List<Message> entityMessages;
    private Message globalMessage;

    public JPMContextImpl() {
        this.fieldMessages = new HashMap<>();
        this.entityMessages = new ArrayList<>();
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Operation getOperation() {
        return operation;
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public EntityInstance getEntityInstance() {
        return entityInstance;
    }

    @Override
    public void setEntityInstance(EntityInstance entityInstance) {
        this.entityInstance = entityInstance;
    }

    @Override
    public void addFieldMsg(final Field field, Message message) {
        if (!getFieldMessages().containsKey(field.getId())) {
            getFieldMessages().put(field.getId(), new ArrayList<Message>());
        }
        getFieldMessages().get(field.getId()).add(message);
    }

    @Override
    public Map<String, List<Message>> getFieldMessages() {
        return fieldMessages;
    }

    public void setFieldMessages(Map<String, List<Message>> fieldMessages) {
        this.fieldMessages = fieldMessages;
    }

    @Override
    public List<Message> getEntityMessages() {
        return entityMessages;
    }

    public void setEntityMessages(List<Message> entityMessages) {
        this.entityMessages = entityMessages;
    }

    @Override
    public void set(Entity entity, Operation operation) {
        this.entity = entity;
        this.operation = operation;
    }

    @Override
    public Message getGlobalMessage() {
        return this.globalMessage;
    }

    @Override
    public void setGlobalMessage(Message message) {
        this.globalMessage = message;
    }
}
