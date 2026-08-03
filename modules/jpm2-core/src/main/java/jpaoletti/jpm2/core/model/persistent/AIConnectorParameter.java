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
import org.hibernate.annotations.Type;

/**
 * A single name/value parameter belonging to an {@link AIConnector}. When {@code encrypted} is set, the
 * {@code value} is stored ciphered (via the app's {@code SysparamCipher}) — used for the {@code api-key}.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "ai_connector_parameters")
public class AIConnectorParameter extends JPMPersistentObject implements EntityParameter {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String value;

    @Type(type = "yes_no")
    private boolean encrypted;

    @ManyToOne(optional = false)
    @JoinColumn(name = "connector")
    private AIConnector connector;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public AIConnector getConnector() {
        return connector;
    }

    public void setConnector(AIConnector connector) {
        this.connector = connector;
    }

    @Override
    public ParameterizedEntity<?> getOwnerEntity() {
        return connector;
    }

    /** No stored type hint: typing/secrecy comes from the catalog (kind "ai-connector"). */
    @Override
    public SysparamType getType() {
        return null;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof AIConnectorParameter;
    }
}
