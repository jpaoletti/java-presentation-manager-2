package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * Contains fields and fields values. It depends on operation.
 *
 * @author jpaoletti
 */
public class EntityInstance {

    private IdentifiedObject iobject;
    private Entity owner;
    private String ownerId;
    private List<Operation> operations; //Individual operations
    private List<Field> fields;
    private Map<String, Object> values;

    /**
     * Used for lists.
     */
    public EntityInstance(IdentifiedObject iobject, List<Field> fields, List<Operation> operations) throws PMException {
        this.iobject = iobject;
        this.values = new LinkedHashMap<>();
        this.operations = operations;
        this.fields = fields;
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
            operations = entity.getOperationsFor(object, operation, OperationScope.ITEM);
            if (entity.isWeak()) {
                this.owner = entity.getOwner().getOwner();
                final Object ownerobject = JPMUtils.get(object, entity.getOwner().getLocalProperty());
                if (ownerobject != null) {
                    this.ownerId = getOwner().getDao().getId(ownerobject).toString();
                }
            }
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

    public final Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getId() {
        return (getIobject() != null) ? getIobject().getId() : "?";
    }
}
