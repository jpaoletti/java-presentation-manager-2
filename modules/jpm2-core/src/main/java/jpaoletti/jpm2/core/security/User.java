package jpaoletti.jpm2.core.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Base Security User. Override with a custom or use JpmUser
 *
 * @author jpaoletti
 */
@MappedSuperclass
public abstract class User implements Serializable, UserDetails {

    @Id
    private String username;
    private String name;
    private String password;
    private String mail;

    @Column(name = "login_attemps")
    private Integer loginAttemps = 0;

    @Type(type = "yes_no")
    private boolean enabled;

    @Type(type = "yes_no")
    @Column(name = "account_non_expired")
    private boolean accountNonExpired;

    @Type(type = "yes_no")
    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Type(type = "yes_no")
    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;

    @ManyToMany(targetEntity = Group.class)
    @JoinTable(name = "group_members", joinColumns
        = @JoinColumn(name = "username"), inverseJoinColumns
        = @JoinColumn(name = "group_id"))
    private List<Group> groups;

    @Transient
    private String newPassword;
    @Transient
    private final List<Authority> authorities;

    public User() {
        this.enabled = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.groups = new ArrayList<>();
        this.authorities = new ArrayList<>();
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public List<Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.username, other.username);
    }

    @Override
    public String toString() {
        if (getUsername() == null) {
            return "...";
        } else if (getName() != null) {
            return getName();
        } else {
            return getUsername();
        }
    }

    public Integer getLoginAttemps() {
        return loginAttemps;
    }

    public void setLoginAttemps(Integer loginAttemps) {
        this.loginAttemps = loginAttemps;
    }

    public String getUserGroups() {
        return getGroups().stream().map(Group::getName).collect(Collectors.joining(", "));
    }
}
