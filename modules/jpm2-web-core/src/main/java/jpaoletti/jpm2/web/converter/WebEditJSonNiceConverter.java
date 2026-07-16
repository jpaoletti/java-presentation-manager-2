package jpaoletti.jpm2.web.converter;

import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Edit converter for {@link JSONObject} (or {@link JSONArray}) fields. Renders a
 * "nice" code-editor style textarea with JSON syntax highlighting and automatic
 * tabulation, pre-filled with the pretty-printed current value.
 *
 * On build, the raw text is parsed back into a {@link JSONObject} (or
 * {@link JSONArray}); an invalid JSON produces a field validation error.
 *
 * @author jpaoletti
 */
public class WebEditJSonNiceConverter extends Converter {

    private int indentFactor = 2;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = getValue(object, field.getProperty());
        String pretty = "";
        if (value instanceof JSONObject) {
            final JSONObject json = (JSONObject) value;
            if (!json.isEmpty()) {
                pretty = json.toString(getIndentFactor());
            }
        } else if (value instanceof JSONArray) {
            final JSONArray json = (JSONArray) value;
            if (!json.isEmpty()) {
                pretty = json.toString(getIndentFactor());
            }
        } else if (value != null && !String.valueOf(value).trim().isEmpty()) {
            pretty = prettify(String.valueOf(value).trim());
        }
        getRequest().setAttribute("editJsonNiceValue", pretty);
        return "@page:edit-json-nice-converter.jsp";
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue == null || String.valueOf(newValue).trim().isEmpty()) {
            return null;
        }
        final String raw = String.valueOf(newValue).trim();
        try {
            if (raw.startsWith("[")) {
                return new JSONArray(raw);
            }
            return new JSONObject(raw);
        } catch (RuntimeException e) {
            throw new ConverterException(MessageFactory.error("jpm.converter.error.invalid.json.format", raw));
        }
    }

    /**
     * Pretty-prints a raw JSON string; returns it unchanged if it is not valid
     * JSON so the user can still see and fix whatever was stored.
     */
    private String prettify(String raw) {
        try {
            if (raw.startsWith("[")) {
                return new JSONArray(raw).toString(getIndentFactor());
            }
            return new JSONObject(raw).toString(getIndentFactor());
        } catch (RuntimeException e) {
            return raw;
        }
    }

    public int getIndentFactor() {
        return indentFactor;
    }

    public void setIndentFactor(int indentFactor) {
        this.indentFactor = indentFactor;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

}
