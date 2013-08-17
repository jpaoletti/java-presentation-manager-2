package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.security.PMSecurityUser;

/**
 * Contains fields and fields values. It depends on operation.
 *
 * @author jpaoletti
 */
public class EntityInstance {

    private List<Operation> operations; //Individual operations
    private List<Field> fields;
    private Map<String, Object> values;

    public EntityInstance(List<Field> fields, List<Operation> operations) throws PMException {
        this.values = new LinkedHashMap<>();
        this.operations = operations;
        this.fields = fields;
    }

    public EntityInstance(Entity entity, Operation operation, PMSecurityUser user, Object object) throws PMException {
        values = new HashMap<>();
        fields = new ArrayList<>();
        operations = new ArrayList<>();
        for (Field field : entity.getAllFields()) {
            final Converter converter = field.getConverter(operation, user);
            if (converter != null) {
                try {
                    values.put(field.getId(), converter.visualize(field, object));
                    fields.add(field);
                } catch (IgnoreConvertionException ex) {
                }
            }
        }
        operations = entity.getOperationsFor(object, operation, OperationScope.ITEM);
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
}
