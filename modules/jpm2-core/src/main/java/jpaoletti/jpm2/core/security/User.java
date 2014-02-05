package jpaoletti.jpm2.core.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Security User. Just for CRUD operations.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "users")
public class User implements Serializable, UserDetails {

    @Id
    private String username;
    private String password;
    private boolean enabled;
    @ManyToMany(targetEntity = Group.class)
    @JoinTable(name = "group_members", joinColumns
            = @JoinColumn(name = "username"), inverseJoinColumns
            = @JoinColumn(name = "group_id"))
    private List<Group> groups;
    @Transient
    private String newPassword;

    public User() {
        this.enabled = true;
        this.groups = new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> res = new ArrayList<>();
        for (Group group : getGroups()) {
            res.addAll(group.getAuthorities());
        }
        return res;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
}
