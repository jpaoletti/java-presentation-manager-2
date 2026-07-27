package jpaoletti.jpm2.core.model.persistent;

import jpaoletti.jpm2.core.cache.CacheType;
import jpaoletti.jpm2.core.model.Duplicable;
import jpaoletti.jpm2.core.model.Exportable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
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
 * Administrable cache region configuration. Each row defines a named cache region
 * ({@code code}) backed by an in-memory map or Redis, plus its connection parameters.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "cache_admins")
public class CacheAdmin extends JPMPersistentObject implements Duplicable, Exportable {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    private String code;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "cache_type")
    private CacheType cacheType;

    @Type(type = "yes_no")
    private Boolean active;

    @OneToMany(mappedBy = "cacheAdmin", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<CacheAdminParameter> parameters = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(CacheType cacheType) {
        this.cacheType = cacheType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<CacheAdminParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<CacheAdminParameter> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getParameterMap() {
        return getParameters().stream().collect(
                Collectors.toMap(CacheAdminParameter::getName,
                        item -> Optional.ofNullable(item.getValue()).orElse(""),
                        (existingValue, newValue) -> existingValue));
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof CacheAdmin;
    }

    @Override
    public Duplicable duplicate() {
        final CacheAdmin res = new CacheAdmin();
        res.setDescription(description);
        res.setCode(code + "*");
        res.setCacheType(cacheType);
        res.setActive(active);
        for (CacheAdminParameter parameter : parameters) {
            final CacheAdminParameter param = (CacheAdminParameter) parameter.duplicate();
            param.setCacheAdmin(res);
            res.getParameters().add(param);
        }
        return res;
    }

    @Override
    public String export() throws PMException {
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("description", getDescription());
        item.put("code", getCode());
        item.put("cacheType", getCacheType() != null ? getCacheType().name() : JSONObject.NULL);
        item.put("active", getActive());

        JSONArray exportedParams = new JSONArray();
        if (getParameters() != null) {
            for (CacheAdminParameter p : getParameters()) {
                JSONObject paramJson = new JSONObject();
                paramJson.put("name", p.getName());
                paramJson.put("value", p.getValue() != null ? p.getValue() : JSONObject.NULL);
                exportedParams.put(paramJson);
            }
        }
        item.put("parameters", exportedParams);
        items.put(item);
        return items.toString(2);
    }

    @Override
    public void importData(String json) throws PMException {
        try {
            JSONObject item = new JSONObject(json);
            setDescription(item.optString("description", null));
            setCode(item.optString("code", null));
            String ct = item.optString("cacheType", null);
            setCacheType(ct != null ? CacheType.valueOf(ct) : null);
            setActive(item.has("active") && !item.isNull("active") ? item.getBoolean("active") : null);

            List<CacheAdminParameter> importedParams = new ArrayList<>();
            JSONArray paramsArray = item.optJSONArray("parameters");
            if (paramsArray != null) {
                for (int i = 0; i < paramsArray.length(); i++) {
                    JSONObject paramJson = paramsArray.getJSONObject(i);
                    CacheAdminParameter p = new CacheAdminParameter();
                    p.setCacheAdmin(this);
                    p.setName(paramJson.optString("name", null));
                    p.setValue(paramJson.optString("value", null));
                    importedParams.add(p);
                }
            }
            setParameters(importedParams);
        } catch (Exception e) {
            throw new PMException("Error importing CacheAdmin: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "...";
        } else {
            return getDescription();
        }
    }
}
