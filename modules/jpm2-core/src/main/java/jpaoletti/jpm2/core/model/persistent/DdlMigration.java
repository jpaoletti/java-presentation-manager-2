package jpaoletti.jpm2.core.model.persistent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Type;

/**
 * History of DDL migration blocks applied at boot by {@code DdlMigrationService}: one row per
 * {@code database.sql} revision ({@code -- @@ N} marker) that was executed, with its outcome.
 * Replaces the silent WARNING logging and the single {@code database-revision} config counter of
 * the legacy runner: the current revision is {@code MAX(revision)} of this table.
 *
 * <p>The table is created by the service itself ({@code CREATE TABLE IF NOT EXISTS}), so it does
 * not depend on {@code hbm2ddl}; this mapping is used for the read-only admin grid.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "jpm_ddl_migration", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"revision"})})
public class DdlMigration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** The {@code -- @@ N} marker number of the applied block. */
    @Column(name = "revision")
    private Integer revision;

    /** The SQL statement that was executed for this revision. */
    @Column(name = "statement", columnDefinition = "longtext")
    private String statement;

    /** Whether the statement executed without error. */
    @Type(type = "yes_no")
    private boolean success;

    /** Error message when {@link #success} is false; null otherwise. */
    @Column(name = "error", columnDefinition = "longtext")
    private String error;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "applied_at")
    private Date appliedAt;

    /** Execution time of the statement, in milliseconds. */
    @Column(name = "duration_ms")
    private Long durationMs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Date appliedAt) {
        this.appliedAt = appliedAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getAppliedAtStr() {
        return appliedAt == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(appliedAt);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DdlMigration)) {
            return false;
        }
        return Objects.equals(this.getId(), ((DdlMigration) obj).getId());
    }

    @Override
    public String toString() {
        return getId() == null ? "..." : ("#" + revision + " " + (success ? "OK" : "FAIL"));
    }
}
