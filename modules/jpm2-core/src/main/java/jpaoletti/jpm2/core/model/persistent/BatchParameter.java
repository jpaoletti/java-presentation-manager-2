package jpaoletti.jpm2.core.model.persistent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A single name/value parameter belonging to a {@link Batch}.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "batchs_parameteres")
public class BatchParameter extends JPMPersistentObject {

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
