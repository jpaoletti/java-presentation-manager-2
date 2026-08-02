package jpaoletti.jpm2.core.model.persistent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import jpaoletti.jpm2.core.entityparam.EntityParameter;
import jpaoletti.jpm2.core.entityparam.ParameterizedEntity;
import jpaoletti.jpm2.core.sysparam.SysparamType;

/**
 * A single name/value parameter belonging to a {@link Batch}. Implements {@link EntityParameter} so its
 * typing/secrecy is driven by the entity-parameter catalog (kind {@code "batch"}) and secret values are
 * encrypted transparently — no stored {@code param_type} column is needed (the type comes from the catalog,
 * so {@link #getType()} is null).
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "batchs_parameteres")
public class BatchParameter extends JPMPersistentObject implements EntityParameter {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String value;

    @ManyToOne(optional = false)
    @JoinColumn(name = "batch")
    private Batch batch;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    @Override
    public ParameterizedEntity<?> getOwnerEntity() {
        return batch;
    }

    /** No stored type hint: typing/secrecy for batch parameters comes from the catalog (kind "batch"). */
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
        return obj instanceof BatchParameter;
    }

}
