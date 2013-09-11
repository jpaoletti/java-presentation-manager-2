package jpaoletti.jpm2.core.model;

import java.util.Arrays;
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
     */
    public SearchDefinition(String fieldId, String paramName, String paramValue) {
        this.fieldId = fieldId;
        this.parameters = new LinkedHashMap<>();
        this.parameters.put(paramName, (String[]) Arrays.asList(paramValue).toArray());
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
