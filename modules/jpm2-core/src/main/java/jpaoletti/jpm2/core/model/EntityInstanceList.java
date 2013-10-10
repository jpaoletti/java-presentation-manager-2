package jpaoletti.jpm2.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;

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

    public void load(final List objects, Entity entity, Operation operation) throws PMException {
        for (Field field : entity.getOrderedFields()) {
            final Converter converter = field.getConverter(operation);
            if (converter != null) {
                getFields().add(field);
                getConverters().put(field.getId(), converter);
            }
        }
        for (Object object : objects) {
            final Serializable instanceId = entity.getDao().getId(object);
            final IdentifiedObject iobject = new IdentifiedObject(String.valueOf(instanceId), object);
            final EntityInstance instance = new EntityInstance(iobject, getFields(), entity, operation);
            for (Field field : getFields()) {
                try {
                    instance.getValues().put(field.getId(), getConverters().get(field.getId()).visualize(field, object, (instanceId != null) ? instanceId.toString() : null));
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
