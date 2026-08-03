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
 * A single name/value parameter belonging to a {@link ThreadRunner}. Typing/secrecy is catalog-driven
 * (kind {@code "thread-runner"}), so no stored {@code param_type} column is needed.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "threads_runners_parameters")
public class ThreadRunnerParameter extends JPMPersistentObject implements EntityParameter {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String value;

    @ManyToOne(optional = false)
    @JoinColumn(name = "thread_runner")
    private ThreadRunner threadRunner;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ThreadRunner getThreadRunner() {
        return threadRunner;
    }

    public void setThreadRunner(ThreadRunner threadRunner) {
        this.threadRunner = threadRunner;
    }

    @Override
    public ParameterizedEntity<?> getOwnerEntity() {
        return threadRunner;
    }

    /** No stored type hint: typing/secrecy comes from the catalog (kind "thread-runner"). */
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
        return obj instanceof ThreadRunnerParameter;
    }

}
