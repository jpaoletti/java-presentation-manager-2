package jpaoletti.jpm2.core.model.persistent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * System log entry.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "syslog")
public class Syslog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name = "event_date")
    private Date eventDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "event_datetime")
    private Date eventDatetime;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    private String permission;
    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getEventDatetime() {
        return eventDatetime;
    }

    public void setEventDatetime(Date eventDatetime) {
        this.eventDatetime = eventDatetime;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Syslog)) {
            return false;
        }
        final Syslog other = (Syslog) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "...";
        }
        return String.format("[%s][%s][%s] %s: %s",
                getEventDatetimeStr(),
                getSeverity().name(),
                getPermission(),
                getUsername(),
                getMessage());
    }

    public String getEventDatetimeStr() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(getEventDatetime());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Log severity level. Values are persisted as strings; they are kept in the
     * original spelling to avoid a data migration of existing rows.
     */
    public static enum Severity {

        Info, Advertencia, Error, Critico;
    }
}
