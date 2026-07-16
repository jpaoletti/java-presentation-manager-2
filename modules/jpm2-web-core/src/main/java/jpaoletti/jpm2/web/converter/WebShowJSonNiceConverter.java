package jpaoletti.jpm2.web.converter;

import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Shows a {@link JSONObject} field pretty-printed (indented) inside a styled
 * &lt;pre&gt; block, so nested JSON structures are displayed in a readable way.
 *
 * Unlike {@link WebShowJSonConverter}, which flattens the JSON into a list of
 * key/value form groups, this converter keeps the full nested structure.
 *
 * @author jpaoletti
 */
public class WebShowJSonNiceConverter extends WebToString {

    private int indentFactor = 2;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object object, String instanceId) throws ConverterException, ConfigurationException {
        if (object == null) {
            return "";
        }
        final String pretty;
        if (object instanceof JSONObject) {
            final JSONObject value = (JSONObject) object;
            if (value.isEmpty()) {
                return "";
            }
            pretty = value.toString(getIndentFactor());
        } else if (object instanceof JSONArray) {
            final JSONArray value = (JSONArray) object;
            if (value.isEmpty()) {
                return "";
            }
            pretty = value.toString(getIndentFactor());
        } else {
            // Fallback: try to parse the string representation as JSON
            final String raw = String.valueOf(object).trim();
            if (raw.isEmpty()) {
                return "";
            }
            String formatted;
            try {
                formatted = new JSONObject(raw).toString(getIndentFactor());
            } catch (RuntimeException e1) {
                try {
                    formatted = new JSONArray(raw).toString(getIndentFactor());
                } catch (RuntimeException e2) {
                    formatted = raw;
                }
            }
            pretty = formatted;
        }
        request.setAttribute("wsjsonc_pretty", pretty);
        return "@page:show-json-nice-converter.jsp";
    }

    public int getIndentFactor() {
        return indentFactor;
    }

    public void setIndentFactor(int indentFactor) {
        this.indentFactor = indentFactor;
    }

}
