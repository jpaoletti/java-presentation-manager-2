package jpaoletti.jpm2.core.model;

import java.io.Serializable;
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

    private Serializable id; //Current intstance Id
    private Entity owner;
    private String ownerId;
    private List<Operation> operations; //Individual operations
    private List<Field> fields;
    private Map<String, Object> values;

    /**
     * Used for lists.
     */
    public EntityInstance(Serializable id, List<Field> fields, List<Operation> operations) throws PMException {
        this.values = new LinkedHashMap<>();
        this.operations = operations;
        this.fields = fields;
        this.id = id;
    }

    /**
     * Used for single instance operations.
     */
    public EntityInstance(Serializable id, Entity entity, Operation operation, Object object) throws PMException {
        this.id = id;
        values = new LinkedHashMap<>();
        fields = new ArrayList<>();
        operations = new ArrayList<>();
        for (Field field : entity.getAllFields()) {
            final Converter converter = field.getConverter(operation);
            if (converter != null) {
                try {
                    values.put(field.getId(), converter.visualize(field, object, (id != null) ? id.toString() : null));
                    fields.add(field);
                } catch (IgnoreConvertionException ex) {
                }
            }
        }
        operations = entity.getOperationsFor(object, operation, OperationScope.ITEM);
        if (entity.isWeak() && object != null) {
            this.owner = entity.getOwner().getOwner();
            final Object ownerobject = JPMUtils.get(object, entity.getOwner().getLocalProperty());
            if (ownerobject != null) {
                this.ownerId = getOwner().getDao().getId(ownerobject).toString();
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

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
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
}
