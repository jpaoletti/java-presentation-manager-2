package jpaoletti.jpm2.core.model.persistent;

import jpaoletti.jpm2.core.model.AuditMaskable;
import jpaoletti.jpm2.core.model.Exportable;
import jpaoletti.jpm2.core.sysparam.SysparamType;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import jpaoletti.jpm2.core.PMException;
import org.hibernate.annotations.Type;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A single administrable system parameter value. The DB row is an override of the
 * code-declared default (see {@code SysparamDef}); descriptive metadata (default,
 * validation) lives in the catalog, not here. Rows with no matching catalog definition
 * are "free" parameters (open key space / dynamic families).
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "jpm_sysparam", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"param_key"})})
public class Sysparam extends JPMPersistentObject implements Exportable, AuditMaskable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "param_key")
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(name = "param_type")
    private SysparamType type = SysparamType.STRING;

    @Column(name = "param_value", columnDefinition = "longtext")
    private String value;

    @Column(name = "param_group")
    private String group;

    @Type(type = "yes_no")
    private boolean cached;

    @Column(name = "read_role")
    private String readRole;

    @Column(name = "write_role")
    private String writeRole;

    @Column(name = "updated_by")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SysparamType getType() {
        return type;
    }

    public void setType(SysparamType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return true if this parameter is secret, i.e. its {@link #type} is
     * {@link SysparamType#SECRET}. Secrecy is derived from the type (not a separate mutable
     * column), so it is catalog-owned and cannot drift from the stored representation.
     */
    @Transient
    public boolean isSecret() {
        return SysparamType.SECRET == type;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public String getReadRole() {
        return readRole;
    }

    public void setReadRole(String readRole) {
        this.readRole = readRole;
    }

    public String getWriteRole() {
        return writeRole;
    }

    public void setWriteRole(String writeRole) {
        this.writeRole = writeRole;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Read-only display value for the admin grid: masked for secrets (never revealing the
     * encrypted blob), the plain stored value otherwise. Not persisted.
     */
    @Transient
    public String getDisplayValue() {
        if (isSecret()) {
            return (value == null) ? "" : "••••••";
        }
        return value;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof Sysparam;
    }

    /** Never expose the secret value in the detailed-audit diff. */
    @Override
    public boolean maskInAudit(String fieldId) {
        return isSecret() && "value".equals(fieldId);
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "...";
        }
        return getKey();
    }

    @Override
    public String export() throws PMException {
        final JSONArray items = new JSONArray();
        final JSONObject item = new JSONObject();
        item.put("key", getKey());
        item.put("type", getType() == null ? JSONObject.NULL : getType().name());
        // Secret values are never exported in clear (or encrypted, since the target
        // environment may use a different key): they must be re-entered per environment.
        item.put("value", isSecret() ? JSONObject.NULL : (getValue() == null ? JSONObject.NULL : getValue()));
        item.put("group", getGroup() == null ? JSONObject.NULL : getGroup());
        item.put("secret", isSecret()); // informational; secrecy is carried by the type
        item.put("cached", isCached());
        item.put("readRole", getReadRole() == null ? JSONObject.NULL : getReadRole());
        item.put("writeRole", getWriteRole() == null ? JSONObject.NULL : getWriteRole());
        items.put(item);
        return items.toString(2);
    }

    @Override
    public void importData(String json) throws PMException {
        try {
            final JSONObject item = new JSONObject(json);
            setKey(item.optString("key", null));
            final String importedType = item.optString("type", null);
            setType((importedType == null || importedType.isBlank()) ? SysparamType.STRING : SysparamType.valueOf(importedType));
            setValue(item.isNull("value") ? null : item.optString("value", null));
            setGroup(item.isNull("group") ? null : item.optString("group", null));
            // Secrecy is derived from the type (SECRET); no separate flag to import.
            setCached(item.optBoolean("cached", true));
            setReadRole(item.isNull("readRole") ? null : item.optString("readRole", null));
            setWriteRole(item.isNull("writeRole") ? null : item.optString("writeRole", null));
        } catch (Exception exception) {
            throw new PMException("Could not import Sysparam", exception);
        }
    }
}
