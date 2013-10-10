package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Contains fields and fields values. It depends on operation.
 *
 * @author jpaoletti
 */
public class EntityInstance {

    private IdentifiedObject iobject;
    private EntityInstanceOwner owner;
    private List<Operation> operations; //Individual operations
    private List<Field> fields;
    private Map<String, Object> values;

    /**
     * Used for lists.
     */
    public EntityInstance(IdentifiedObject iobject, List<Field> fields, Entity entity, Operation operation) throws PMException {
        this.iobject = iobject;
        this.values = new LinkedHashMap<>();
        this.fields = fields;
        configureItemOperations(entity, operation);
    }

    /**
     * Used for single instance operations.
     */
    public EntityInstance(Entity entity, Operation operation) throws PMException {
        this(null, entity, operation);
    }

    /**
     * Used for single instance operations.
     */
    public EntityInstance(IdentifiedObject iobject, Entity entity, Operation operation) throws PMException {
        this.iobject = iobject;
        final Object object = (iobject != null) ? iobject.getObject() : null;
        final String id = (iobject != null) ? iobject.getId() : null;
        values = new LinkedHashMap<>();
        fields = new ArrayList<>();
        operations = new ArrayList<>();
        for (Field field : entity.getOrderedFields()) {
            final Converter converter = field.getConverter(operation);
            if (converter != null) {
                try {
                    values.put(field.getId(), converter.visualize(field, object, id));
                    fields.add(field);
                } catch (IgnoreConvertionException ex) {
                }
            }
        }
        if (object != null) {
            if (entity.isWeak()) {
                final Object ownerobject = JPMUtils.get(object, entity.getOwner().getLocalProperty());
                configureOwner(entity, ownerobject);
            }
            configureItemOperations(entity, operation);
        }
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Map<String, Object> getValues() {
        return values;
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

    public String getOwnerId() {
        return getOwner().getIobject().getId();
    }

    public String getId() {
        return (getIobject() != null) ? getIobject().getId() : "?";
    }

    protected final void configureItemOperations(Entity entity, Operation operation) throws PMException {
        this.operations = entity.getOperationsFor(this, operation, OperationScope.ITEM);
    }

    public final void configureOwner(Entity entity, final Object ownerobject) {
        final Entity ownerEntity = entity.getOwner().getOwner();
        if (ownerobject != null) {
            final String ownerId = String.valueOf(ownerEntity.getDao().getId(ownerobject));
            this.owner = new EntityInstanceOwner(ownerEntity, new IdentifiedObject(ownerId, ownerobject));
        } else {
            this.owner = new EntityInstanceOwner(ownerEntity, null);
        }
    }
}
