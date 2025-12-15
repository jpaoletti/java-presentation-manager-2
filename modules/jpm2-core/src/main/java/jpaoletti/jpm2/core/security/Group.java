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
@Table(name = "jpm_groups")
public class Group implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "group_name")
    private String name;

    /**
     * Hierarchy level: 1 = maximum privilege, higher values = lower privilege.
     * This allows infinite scaling downwards (adding less privileged roles).
     */
    @Column(name = "hierarchy_level", nullable = false)
    private Integer level = 999; // Default: minimum privilege (safe default)

    // Using generics in place of a targetClass
    @ElementCollection
    @CollectionTable(name = "group_authorities", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "authority")
    private List<String> authorities;

    // Constants for common privilege levels
    public static final Integer LEVEL_MAX_PRIVILEGE = 1;
    public static final Integer LEVEL_HIGH_PRIVILEGE = 2;
    public static final Integer LEVEL_MEDIUM_PRIVILEGE = 3;
    public static final Integer LEVEL_LOW_PRIVILEGE = 4;
    public static final Integer LEVEL_MIN_PRIVILEGE = 5;

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
            return getName() == null ? "" : getName();
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * Verifies if this group can manage another group based on hierarchy level.
     * A group can manage another if its level is LOWER OR EQUAL (higher privilege).
     *
     * @param otherGroup The group to check
     * @return true if this group has equal or higher privilege (lower level number)
     */
    public boolean canManage(Group otherGroup) {
        if (otherGroup == null) {
            return false;
        }
        return this.level <= otherGroup.getLevel();
    }
}
