package jpaoletti.jpm2.core.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author jpaoletti
 */
public class SearchDefinition {

    private String fieldId;
    private Map<String, String[]> parameters;

    public SearchDefinition() {
    }

    /**
     * Single param constructor
     *
     * @param fieldId
     * @param paramName
     * @param paramValue
     */
    public SearchDefinition(String fieldId, String paramName, String paramValue) {
        this.fieldId = fieldId;
        this.parameters = new LinkedHashMap<>();
        final String[] params = new String[1];
        params[0] = paramValue;
        this.parameters.put(paramName, params);
    }

    public SearchDefinition(String fieldId, Map<String, String[]> parameters) {
        this.fieldId = fieldId;
        this.parameters = parameters;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }
}
