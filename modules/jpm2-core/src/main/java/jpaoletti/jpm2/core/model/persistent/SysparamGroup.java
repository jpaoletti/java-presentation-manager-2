package jpaoletti.jpm2.core.model.persistent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Type;

/**
 * Presentation overlay for a sysparam {@code group}: purely aesthetic, admin-editable metadata
 * (icon, color, collapsed state, order) used to render the group nodes of the sysparam tree
 * view. It is independent of the parameter catalog — groups are discovered from the parameters
 * themselves; a group with no row here simply renders with defaults.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "jpm_sysparam_group", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name"})})
public class SysparamGroup extends JPMPersistentObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** Matches the {@code group} value of the parameters this styling applies to. */
    @Column(name = "name")
    private String name;

    /** Optional pretty label shown in the tree; falls back to {@link #name} when empty. */
    @Column(name = "label")
    private String label;

    /** Font Awesome icon class, e.g. {@code fas fa-envelope}. */
    @Column(name = "icon")
    private String icon;

    /** Raw CSS style applied to the group icon in the tree (same idea as tag styles). */
    @Column(name = "style")
    private String style;

    /** Whether the group node starts collapsed in the tree. */
    @Type(type = "yes_no")
    private boolean collapsed;

    /** Ordering weight among groups (ascending); ties broken by name. */
    @Column(name = "sort_order")
    private Integer sortOrder;

    @Override
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof SysparamGroup;
    }

    @Override
    public String toString() {
        return getId() == null ? "..." : getName();
    }
}
