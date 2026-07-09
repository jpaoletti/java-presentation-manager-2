package jpaoletti.jpm2.core.model.persistent;

import jpaoletti.jpm2.core.model.Duplicable;
import jpaoletti.jpm2.core.model.Exportable;
import jpaoletti.jpm2.core.mail.MailSenderType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import jpaoletti.jpm2.core.PMException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Administrable mail sender configuration.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "mail_senders")
public class MailSender extends JPMPersistentObject implements Duplicable, Exportable {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;

    @Enumerated
    @Column(name = "sender_type")
    private MailSenderType senderType = MailSenderType.SMTP;

    @Type(type = "yes_no")
    private boolean enabled;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<MailSenderParameter> parameters = new ArrayList<>();

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

    public List<MailSenderParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<MailSenderParameter> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name, String def) {
        final MailSenderParameter config = getParameter(name);
        if (config == null) {
            return def;
        } else {
            return config.getValue();
        }
    }

    public MailSenderParameter getParameter(String name) {
        if (getParameters() == null) {
            return null;
        }
        for (MailSenderParameter c : getParameters()) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public Integer getParameter(String name, Integer def) {
        try {
            return Integer.parseInt(getParameter(name, (def == null) ? null : def.toString()));
        } catch (Exception exception) {
            return def;
        }
    }

    public Long getParameter(String name, Long def) {
        return Long.parseLong(getParameter(name, (def == null) ? null : def.toString()));
    }

    public boolean getParameter(String name, boolean def) {
        return Boolean.parseBoolean(getParameter(name, Boolean.toString(def)));
    }

    public Map<String, String> getParameterMap() {
        final Map<String, String> result = new HashMap<>();
        getParameters().stream().forEach(param -> {
            result.put(param.getName(), getParameter(param.getName()).getValue());
        });
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

    public MailSenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(MailSenderType senderType) {
        this.senderType = senderType;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof MailSender;
    }

    @Override
    public Duplicable duplicate() {
        final MailSender res = new MailSender();
        res.setDescription(description);
        res.setEnabled(enabled);
        res.setName(name + "*");
        res.setSenderType(senderType);
        for (MailSenderParameter parameter : parameters) {
            final MailSenderParameter param = (MailSenderParameter) parameter.duplicate();
            param.setSender(res);
            res.getParameters().add(param);
        }
        return res;
    }

    @Override
    public String export() throws PMException {
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("name", getName());
        item.put("description", getDescription());
        item.put("senderType", (getSenderType() == null) ? JSONObject.NULL : getSenderType().name());
        item.put("enabled", isEnabled());

        JSONArray exportedParameters = new JSONArray();
        if (getParameters() != null) {
            for (MailSenderParameter parameter : getParameters()) {
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
            String importedSenderType = item.optString("senderType", null);
            setSenderType((importedSenderType == null || importedSenderType.isBlank()) ? null : MailSenderType.valueOf(importedSenderType));
            setEnabled(item.optBoolean("enabled", false));

            List<MailSenderParameter> importedParameters = new ArrayList<>();
            JSONArray imported = item.optJSONArray("parameters");
            if (imported != null) {
                for (int i = 0; i < imported.length(); i++) {
                    JSONObject parameterJson = imported.getJSONObject(i);
                    MailSenderParameter parameter = new MailSenderParameter();
                    parameter.setSender(this);
                    parameter.setName(parameterJson.optString("name", null));
                    parameter.setValue(parameterJson.optString("value", null));
                    importedParameters.add(parameter);
                }
            }
            setParameters(importedParameters);
        } catch (Exception exception) {
            throw new PMException("No se pudo importar el MailSender", exception);
        }
    }
}
