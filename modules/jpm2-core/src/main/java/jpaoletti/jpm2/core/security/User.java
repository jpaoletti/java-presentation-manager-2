package jpaoletti.jpm2.core.security;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Security User. Just for CRUD operations.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    private String username;
    private String password;
    private boolean enabled;
    @ManyToMany(targetEntity = Group.class)
    @JoinTable(name = "group_members", joinColumns =
            @JoinColumn(name = "username"), inverseJoinColumns =
            @JoinColumn(name = "group_id"))
    private List<Group> groups;

    public User() {
        this.enabled = true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return getUsername();
    }
}
