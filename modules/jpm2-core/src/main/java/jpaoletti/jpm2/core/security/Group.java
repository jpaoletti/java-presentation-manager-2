package jpaoletti.jpm2.core.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * User group. Just for CRUD operations.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "groups")
public class Group implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "group_name")
    private String name;
    // Using generics in place of a targetClass
    @ElementCollection
    @CollectionTable(name = "group_authorities", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "authority")
    private List<String> authorities;

    public Group() {
        this.authorities = new ArrayList<>();
    }

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

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "...";
        } else {
            return getName();
        }
    }

    public boolean hasRole(String role) {
        return getAuthorities().contains(role);
    }

    public List<Authority> getGrantedAuthority() {
        List<Authority> res = new ArrayList<>();
        for (String authority : getAuthorities()) {
            res.add(new Authority(authority));
        }
        return res;
    }

}
