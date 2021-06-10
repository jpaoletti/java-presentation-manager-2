package jpaoletti.jpm2.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

/**
 * WORK IN PROCESS
 * @author jpaoletti
 */
@javax.persistence.Entity
@Table(name = "jpm_user_searchs")
public class UserSearch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = true)
    private String username;

    private String name;

    @Column(nullable = false)
    private String entity;

    @Column(nullable = false)
    private String field;

    @Type(type = "yes_no")
    private boolean enabled;

    @Column(nullable = true, columnDefinition = "LONGTEXT")
    private String parameters;

    public UserSearch() {
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
