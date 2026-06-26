package jpaoletti.jpm2.core.model.persistent;

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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "notifications")
public class Notification extends JPMPersistentObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "description", length = 10000)
    private String description;

    @Column(name = "username")
    private String username;

    @Type(type = "yes_no")
    @Column(name = "`read`")
    private boolean read = false;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private NotificationType type = NotificationType.None;

    @Column(name = "created_at")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createdAt;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "...";
        }
        return String.format("[%s] %s", getType().toString(), username);
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof Notification;
    }

}
