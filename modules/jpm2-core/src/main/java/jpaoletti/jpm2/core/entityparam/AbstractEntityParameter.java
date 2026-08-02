package jpaoletti.jpm2.core.entityparam;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import jpaoletti.jpm2.core.sysparam.SysparamType;

/**
 * Optional {@code @MappedSuperclass} base that hoists the {@code name} / {@code value} / {@code param_type}
 * columns and accessors for a parameter child. Consumers that can root their child entity here extend it and
 * only need to add their own {@code @Id} and the concrete {@code @ManyToOne} back-reference (Hibernate cannot
 * map a generic association, so those stay concrete per subclass).
 *
 * <p>It deliberately declares neither {@code @Id} nor {@code getId()}/{@code isValidClass()} so it composes
 * with either {@code JPMPersistentObject} or {@code CustomModelObject} descendants. Entities that already root
 * elsewhere can instead just {@code implements EntityParameter} and declare the columns themselves.
 *
 * @author jpaoletti
 */
@MappedSuperclass
public abstract class AbstractEntityParameter implements EntityParameter {

    /** Mask shown for secret values in the UI (mirrors the Sysparam mask). */
    public static final String MASK = "******";

    private String name;
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "param_type")
    private SysparamType type;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public SysparamType getType() {
        return type;
    }

    public void setType(SysparamType type) {
        this.type = type;
    }

    /** Display helper: masks secret values, never emitting the encrypted blob. */
    public String getDisplayValue() {
        return type == SysparamType.SECRET ? MASK : value;
    }
}
