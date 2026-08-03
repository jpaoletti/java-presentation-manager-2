package jpaoletti.jpm2.core.model.persistent;

import jpaoletti.jpm2.core.entityparam.EntityParameter;
import jpaoletti.jpm2.core.entityparam.ParameterizedEntity;
import jpaoletti.jpm2.core.model.Duplicable;
import jpaoletti.jpm2.core.sysparam.SysparamType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A single name/value parameter belonging to a {@link CacheAdmin}.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "cache_admins_parameters")
public class CacheAdminParameter extends JPMPersistentObject implements Duplicable, EntityParameter {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String value;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cache_admin")
    private CacheAdmin cacheAdmin;

    public CacheAdminParameter() {
    }

    public CacheAdminParameter(String name, String value, CacheAdmin cacheAdmin) {
        this.name = name;
        this.value = value;
        this.cacheAdmin = cacheAdmin;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CacheAdmin getCacheAdmin() {
        return cacheAdmin;
    }

    public void setCacheAdmin(CacheAdmin cacheAdmin) {
        this.cacheAdmin = cacheAdmin;
    }

    @Override
    public ParameterizedEntity<?> getOwnerEntity() {
        return cacheAdmin;
    }

    /** No stored type hint: typing/secrecy comes from the catalog (kind "cache-admin"). */
    @Override
    public SysparamType getType() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof CacheAdminParameter;
    }

    @Override
    public Duplicable duplicate() {
        final CacheAdminParameter res = new CacheAdminParameter();
        res.setName(name);
        res.setValue(value);
        return res;
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "...";
        }
        return getName();
    }

}
