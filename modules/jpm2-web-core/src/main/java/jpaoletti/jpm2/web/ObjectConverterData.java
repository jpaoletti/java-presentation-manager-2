package jpaoletti.jpm2.web;

import java.util.List;
import java.util.regex.Matcher;
import static jpaoletti.jpm2.core.converter.ToStringConverter.DISPLAY_PATTERN;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class ObjectConverterData {

    private List<ObjectConverterDataItem> results;
    private boolean more;

    public List<ObjectConverterDataItem> getResults() {
        return results;
    }

    public void setResults(List<ObjectConverterDataItem> results) {
        this.results = results;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public static ObjectConverterDataItem buildDataObject(String textField, Entity entity, final String entityContext, String instanceId, final Object object) throws ConfigurationException {
        ObjectConverterDataItem result;
        if (textField != null) {
            if (!textField.contains("{")) {
                final Field field = entity.getFieldById(textField, entityContext);
                return new ObjectConverterDataItem(instanceId, String.valueOf(JPMUtils.get(object, field.getProperty())));
            } else {
                result = new ObjectConverterDataItem(instanceId, renderTextField(textField, entity, entityContext, object));
            }
        } else {
            result = new ObjectConverterDataItem(instanceId, object.toString());
        }
        return result;
    }

    public static String renderTextField(String textField, Entity entity, final String entityContext, final Object object) throws ConfigurationException {
        final Matcher matcher = DISPLAY_PATTERN.matcher(textField);
        String finalValue = textField;
        while (matcher.find()) {
            final String displayField = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
            finalValue = finalValue.replace("{" + displayField + "}", resolveDisplayValue(displayField, entity, entityContext, object));
        }
        return finalValue;
    }

    private static String resolveDisplayValue(String displayField, Entity entity, final String entityContext, final Object object) throws ConfigurationException {
        final String cleanField = displayField.replace("!", "");
        final int defaultSeparator = cleanField.indexOf('|');
        final String fieldId = defaultSeparator >= 0 ? cleanField.substring(0, defaultSeparator) : cleanField;
        final String defaultValue = defaultSeparator >= 0 ? cleanField.substring(defaultSeparator + 1) : null;
        final Field field = entity.getFieldById(fieldId, entityContext);
        final Object value = JPMUtils.get(object, field.getProperty());
        if (value == null) {
            return defaultValue == null ? "null" : defaultValue;
        }
        final String stringValue = String.valueOf(value);
        return "null".equals(stringValue) && defaultValue != null ? defaultValue : stringValue;
    }

    public static class ObjectConverterDataItem {

        private String id;
        private String text;

        public ObjectConverterDataItem(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public ObjectConverterDataItem() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
