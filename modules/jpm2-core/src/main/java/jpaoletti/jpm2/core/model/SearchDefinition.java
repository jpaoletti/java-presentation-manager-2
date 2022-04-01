package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jpaoletti
 */
public class SearchDefinition {

    private String fieldId;
    private Map<String, List<String>> parameters;

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
        final List<String> params = new ArrayList<>();
        params.add(paramValue);
        this.parameters.put(paramName, params);
    }

    public SearchDefinition(String fieldId, Map<String, List<String>> parameters) {
        this.fieldId = fieldId;
        this.parameters = parameters;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String[]> getParametersForBuild() {
        final Map<String, String[]> res = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, List<String>> e : parameters.entrySet()) {
            String key = e.getKey();
            List<String> value = e.getValue();
            res.put(key, value.toArray(new String[value.size()]));
        }
        return res;
    }

}
