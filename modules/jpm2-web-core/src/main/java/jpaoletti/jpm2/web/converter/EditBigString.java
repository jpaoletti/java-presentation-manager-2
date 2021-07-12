package jpaoletti.jpm2.web.converter;

import java.util.LinkedHashMap;
import java.util.Map;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class EditBigString extends Converter {

    private final Map<String, String> replaces = new LinkedHashMap<>(); //used to avoid html character loose

    public EditBigString() {
        replaces.put("&", "&amp;");
        replaces.put(">", "&gt;");
        replaces.put("<", "&lt;");
    }

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final String value = (String) object;
        String v = value != null ? value : "";
        for (Map.Entry<String, String> entry : replaces.entrySet()) {
            v = v.replaceAll(entry.getKey(), entry.getValue());
        }
        return "<textarea class='form-control' name='field_" + field.getId() + "'>" + v + "</textarea>";
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        return newValue;
    }
}
