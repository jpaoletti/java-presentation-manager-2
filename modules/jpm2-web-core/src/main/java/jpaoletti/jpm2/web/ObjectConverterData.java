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
                final Matcher matcher = DISPLAY_PATTERN.matcher(textField);
                String finalValue = textField;
                while (matcher.find()) {
                    final String _display_field = matcher.group().replaceAll("\\{", "").replaceAll("\\}", "");
                    final Field field2 = entity.getFieldById(_display_field.replaceAll("\\!", ""), entityContext);
                    finalValue = finalValue.replace("{" + _display_field + "}", String.valueOf(JPMUtils.get(object, field2.getProperty())));
                }
                result = new ObjectConverterDataItem(instanceId, finalValue);
            }
        } else {
            result = new ObjectConverterDataItem(instanceId, object.toString());
        }
        return result;
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
