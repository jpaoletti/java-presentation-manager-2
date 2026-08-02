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
import jpaoletti.jpm2.core.entityparam.EntityParameterDef;
import jpaoletti.jpm2.core.entityparam.EntityParameterModule;
import jpaoletti.jpm2.core.entityparam.EntityParameterResolver;
import jpaoletti.jpm2.core.entityparam.ParameterizedEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a scheduled background job.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "batchs")
public class Batch extends JPMPersistentObject implements Exportable, ParameterizedEntity<BatchParameter>, EntityParameterModule {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String task; // Spring bean id

    @Type(type = "yes_no")
    private boolean enabled;

    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchParameter> parameters = new ArrayList<>();

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

    public List<BatchParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<BatchParameter> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name, String def) {
        return EntityParameterResolver.get(this, name, def);
    }

    public BatchParameter getParameter(String name) {
        if (getParameters() == null) {
            return null;
        }
        for (BatchParameter c : getParameters()) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public Integer getParameter(String name, Integer def) {
        return EntityParameterResolver.get(this, name, def);
    }

    public Long getParameter(String name, Long def) {
        return EntityParameterResolver.get(this, name, def);
    }

    public boolean getParameter(String name, boolean def) {
        return EntityParameterResolver.get(this, name, def);
    }

    public List<String> getParameter(String name, List<String> def) {
        return EntityParameterResolver.get(this, name, def);
    }

    public Map<String, String> getParameterMap() {
        return EntityParameterResolver.map(this);
    }

    @Override
    public String getParameterKind() {
        return BatchParamCatalog.KIND;
    }

    /** General batch params (the ones proper to a task are declared by its task bean, composed here). */
    @Override
    public List<EntityParameterDef<?>> params() {
        return BatchParamCatalog.general();
    }

    /** Effective catalog of this batch: general + those of its {@code task} bean. */
    @Override
    public List<EntityParameterDef<?>> parameterCatalog() {
        return BatchParamCatalog.effective(getTask());
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

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof Batch;
    }

    public String getHour() {
        return getParameter("hour", getParameter("hora", ""));
    }

    public BatchParameter setParameter(String param, String value) {
        BatchParameter parameter = getParameter(param);
        if (parameter == null) {
            parameter = new BatchParameter();
            parameter.setBatch(this);
            parameter.setName(param);
        }
        parameter.setValue(value);
        return parameter;
    }

    @Override
    public String export() throws PMException {
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("name", getName());
        item.put("description", getDescription());
        item.put("task", getTask());
        item.put("enabled", isEnabled());

        JSONArray exportedParameters = new JSONArray();
        if (getParameters() != null) {
            for (BatchParameter parameter : getParameters()) {
                JSONObject exportedParameter = new JSONObject();
                exportedParameter.put("name", parameter.getName());
                // Never export secret values (they are re-entered per environment).
                final boolean secret = EntityParameterResolver.isSecret(this, parameter.getName());
                exportedParameter.put("value",
                        (secret || parameter.getValue() == null) ? JSONObject.NULL : parameter.getValue());
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
            setTask(item.optString("task", null));
            setEnabled(item.optBoolean("enabled", false));

            List<BatchParameter> importedParameters = new ArrayList<>();
            JSONArray imported = item.optJSONArray("parameters");
            if (imported != null) {
                for (int i = 0; i < imported.length(); i++) {
                    JSONObject parameterJson = imported.getJSONObject(i);
                    BatchParameter parameter = new BatchParameter();
                    parameter.setBatch(this);
                    parameter.setName(parameterJson.optString("name", null));
                    parameter.setValue(parameterJson.optString("value", null));
                    importedParameters.add(parameter);
                }
            }
            setParameters(importedParameters);
        } catch (Exception exception) {
            throw new PMException("No se pudo importar el Batch", exception);
        }
    }
}
