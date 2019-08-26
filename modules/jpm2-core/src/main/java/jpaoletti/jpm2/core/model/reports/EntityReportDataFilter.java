package jpaoletti.jpm2.core.model.reports;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jpaoletti
 */
public class EntityReportDataFilter {

    private String field;
    private List<EntityReportDataFilterParameter> parameters;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<EntityReportDataFilterParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<EntityReportDataFilterParameter> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String[]> getParameterMap() {
        final Map<String, String[]> res = new LinkedHashMap<>();
        for (EntityReportDataFilterParameter parameter : getParameters()) {
            final String[] value = new String[1];
            value[0] = parameter.getValue();
            res.put(parameter.getName(), value);
        }
        return res;
    }

}
