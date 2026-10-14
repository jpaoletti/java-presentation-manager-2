package jpaoletti.jpm2.core.model.persistent;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

/**
 * Item of the dynamic menu, administrable from the {@code jpmMenu} entity. The
 * items form a tree through {@link #parent}; only {@link JpmMenuType#GROUP}
 * items have children.
 *
 * <p>Opt-in module: import {@code classpath:entities/jpmMenu.xml}, register this
 * class in the sessionFactory, create the {@code jpm_menu} table and turn on the
 * {@code jpm.menu.dynamic} sysparam.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "jpm_menu")
public class JpmMenu extends JPMPersistentObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private JpmMenu parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("sortOrder, id")
    private List<JpmMenu> children = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private JpmMenuType type = JpmMenuType.ENTITY;

    /** Literal text or a messages.properties key. */
    @Column(name = "label")
    private String label;

    /** CSS class of the icon, e.g. {@code fas fa-boxes}. */
    @Column(name = "icon", length = 100)
    private String icon;

    /** Entity id (ENTITY items); accepts {@code entity!context}. */
    @Column(name = "entity", length = 100)
    private String entity;

    /** Operation id (ENTITY items); {@code list} when empty. */
    @Column(name = "operation", length = 100)
    private String operation;

    /** URL (URL items): relative to the context path or absolute. */
    @Column(name = "url", length = 500)
    private String url;

    /** Comma separated authorities; the user needs any of them. Optional. */
    @Column(name = "authorities", length = 1000)
    private String authorities;

    /**
     * Optional condition on the logged user: {@code user.<property>} shows the
     * item only when the property has a value, {@code !user.<property>} only
     * when it is empty.
     */
    @Column(name = "user_condition")
    private String userCondition;

    /** Ordering weight among siblings (ascending); ties broken by id. */
    @Column(name = "sort_order")
    private Integer sortOrder;

    @Type(type = "yes_no")
    @Column(name = "active")
    private boolean active = true;

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof JpmMenu;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpmMenu getParent() {
        return parent;
    }

    public void setParent(JpmMenu parent) {
        this.parent = parent;
    }

    public List<JpmMenu> getChildren() {
        return children;
    }

    public void setChildren(List<JpmMenu> children) {
        this.children = children;
    }

    public JpmMenuType getType() {
        return type;
    }

    public void setType(JpmMenuType type) {
        this.type = type;
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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public String getUserCondition() {
        return userCondition;
    }

    public void setUserCondition(String userCondition) {
        this.userCondition = userCondition;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        if (label != null && !label.isEmpty()) {
            return label;
        }
        return type + (entity != null ? " " + entity : "") + (url != null ? " " + url : "");
    }
}
