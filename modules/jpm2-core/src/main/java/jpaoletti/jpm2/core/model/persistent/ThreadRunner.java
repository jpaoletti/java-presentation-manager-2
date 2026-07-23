package jpaoletti.jpm2.core.model.persistent;

import jpaoletti.jpm2.core.model.Exportable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import jpaoletti.jpm2.core.PMException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Background thread runner definition.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "threads_runners")
public class ThreadRunner extends JPMPersistentObject implements Exportable {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String clazz;

    @Type(type = "yes_no")
    private boolean enabled;

    @Type(type = "yes_no")
    @NotFound(action = NotFoundAction.IGNORE)
    private boolean debug = false;

    @OneToMany(mappedBy = "threadRunner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThreadRunnerParameter> parameters = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public List<ThreadRunnerParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ThreadRunnerParameter> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name, String def) {
        final ThreadRunnerParameter config = getParameter(name);
        if (config == null) {
            return def;
        } else {
            return config.getValue();
        }
    }

    public ThreadRunnerParameter getParameter(String name) {
        if (getParameters() == null) {
            return null;
        }
        for (ThreadRunnerParameter c : getParameters()) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public Integer getParameter(String name, Integer def) {
        try {
            return Integer.valueOf(getParameter(name, (def == null) ? null : def.toString()));
        } catch (Exception exception) {
            return def;
        }
    }

    public Long getParameter(String name, Long def) {
        return Long.valueOf(getParameter(name, (def == null) ? null : def.toString()));
    }

    public boolean getParameter(String name, boolean def) {
        return Boolean.parseBoolean(getParameter(name, Boolean.toString(def)));
    }

    public List<String> getParameter(String name, List<String> def) {
        final ThreadRunnerParameter config = getParameter(name);
        if (config == null || StringUtils.isEmpty(config.getValue())) {
            return def;
        } else {
            return Arrays.asList(config.getValue().split("[,]"));
        }
    }

    public Map<String, String> getParameterMap() {
        final Map<String, String> result = new HashMap<>();
        getParameters().stream().forEach(param -> {
            result.put(param.getName(), getParameter(param.getName()).getValue());
        });
        result.put("debug", Boolean.toString(isDebug()));
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof ThreadRunner;
    }

    public String getDoWork() {
        return getParameter("do-work", "true");
    }

    public ThreadRunnerParameter setParameter(String param, String value) {
        ThreadRunnerParameter parameter = getParameter(param);
        if (parameter == null) {
            parameter = new ThreadRunnerParameter();
            parameter.setThreadRunner(this);
            parameter.setName(param);
        }
        parameter.setValue(value);
        return parameter;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public String export() throws PMException {
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("name", getName());
        item.put("description", getDescription());
        item.put("clazz", getClazz());
        item.put("enabled", isEnabled());
        item.put("debug", isDebug());

        JSONArray exportedParameters = new JSONArray();
        if (getParameters() != null) {
            for (ThreadRunnerParameter parameter : getParameters()) {
                JSONObject exportedParameter = new JSONObject();
                exportedParameter.put("name", parameter.getName());
                exportedParameter.put("value", parameter.getValue());
                exportedParameters.put(exportedParameter);
            }
        }
        item.put("parameters", exportedParameters);
        items.put(item);
        return items.toString(2);
    }

    @Override
    public void importData(String json) throws PMException {
        try {
            JSONObject item = new JSONObject(json);
            setName(item.optString("name", null));
            setDescription(item.optString("description", null));
            setClazz(item.optString("clazz", null));
            setEnabled(item.optBoolean("enabled", false));
            setDebug(item.optBoolean("debug", false));

            List<ThreadRunnerParameter> importedParameters = new ArrayList<>();
            JSONArray imported = item.optJSONArray("parameters");
            if (imported != null) {
                for (int i = 0; i < imported.length(); i++) {
                    JSONObject parameterJson = imported.getJSONObject(i);
                    ThreadRunnerParameter parameter = new ThreadRunnerParameter();
                    parameter.setThreadRunner(this);
                    parameter.setName(parameterJson.optString("name", null));
                    parameter.setValue(parameterJson.optString("value", null));
                    importedParameters.add(parameter);
                }
            }
            setParameters(importedParameters);
        } catch (Exception exception) {
            throw new PMException("No se pudo importar el ThreadRunner", exception);
        }
    }
}
