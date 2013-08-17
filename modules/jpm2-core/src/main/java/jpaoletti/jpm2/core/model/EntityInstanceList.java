package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.security.PMSecurityUser;

/**
 *
 * @author jpaoletti
 */
public class EntityInstanceList extends ArrayList<EntityInstance> {

    private List<Field> fields;
    private Map<String, Converter> converters;

    public EntityInstanceList() {
        this.fields = new ArrayList<>();
        this.converters = new HashMap<>();
    }

    public void load(final List objects, Entity entity, Operation operation, PMSecurityUser user) throws PMException {
        fields = new ArrayList<>();
        converters = new HashMap<>();
        for (Field field : entity.getAllFields()) {
            final Converter converter = field.getConverter(operation, user);
            if (converter != null) {
                fields.add(field);
                converters.put(field.getId(), converter);
            }
        }
        for (Object object : objects) {
            final EntityInstance instance = new EntityInstance(fields, entity.getOperationsFor(object, operation, OperationScope.ITEM));
            for (Field field : getFields()) {
                try {
                    instance.getValues().put(field.getId(), converters.get(field.getId()).visualize(field, object));
                } catch (IgnoreConvertionException ex) {
                }
            }
            add(instance);
        }
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Map<String, Converter> getConverters() {
        return converters;
    }

    public void setConverters(Map<String, Converter> converters) {
        this.converters = converters;
    }
}
