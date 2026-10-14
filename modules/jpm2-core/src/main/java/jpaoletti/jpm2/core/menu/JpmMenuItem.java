package jpaoletti.jpm2.core.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jpaoletti.jpm2.core.model.persistent.JpmMenuType;
import org.apache.commons.lang3.StringUtils;

/**
 * Immutable copy of a {@code jpm_menu} row, as cached by
 * {@link JpmMenuService}. It does not depend on the Hibernate session.
 *
 * @author jpaoletti
 */
public class JpmMenuItem implements Serializable {

    public static final String DEFAULT_OPERATION = "list";

    private final Long id;
    private final JpmMenuType type;
    private final String label;
    private final String icon;
    private final String entity;
    private final String operation;
    private final String operationPath;
    private final String url;
    private final List<String> authorities;
    private String userCondition;
    private final boolean active;
    private final String invalidReason;
    private final List<JpmMenuItem> children;

    public JpmMenuItem(Long id, JpmMenuType type, String label, String icon, String entity, String operation,
            String url, String authorities, boolean active, String invalidReason, List<JpmMenuItem> children) {
        this(id, type, label, icon, entity, operation, null, url, authorities, active, invalidReason, children);
    }

    /**
     * @param operationPath path of the operation in the url ({@code Operation.getPathId()}, e.g.
     * {@code report.exec}); the operation id when null
     */
    public JpmMenuItem(Long id, JpmMenuType type, String label, String icon, String entity, String operation, String operationPath,
            String url, String authorities, boolean active, String invalidReason, List<JpmMenuItem> children) {
        this.id = id;
        this.type = type == null ? JpmMenuType.ENTITY : type;
        this.label = StringUtils.trimToNull(label);
        this.icon = StringUtils.trimToNull(icon);
        this.entity = StringUtils.trimToNull(entity);
        this.operation = StringUtils.defaultIfBlank(StringUtils.trimToNull(operation), DEFAULT_OPERATION);
        this.operationPath = StringUtils.defaultIfBlank(operationPath, this.operation);
        this.url = StringUtils.trimToNull(url);
        this.authorities = splitAuthorities(authorities);
        this.active = active;
        this.invalidReason = invalidReason;
        this.children = children == null ? Collections.emptyList() : Collections.unmodifiableList(children);
    }

    public static List<String> splitAuthorities(String authorities) {
        final List<String> res = new ArrayList<>();
        if (authorities != null) {
            for (String a : authorities.split("[,]")) {
                if (StringUtils.isNotBlank(a)) {
                    res.add(a.trim());
                }
            }
        }
        return Collections.unmodifiableList(res);
    }

    public Long getId() {
        return id;
    }

    public JpmMenuType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public String getEntity() {
        return entity;
    }

    /**
     * @return the operation id, {@code list} when not configured.
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @return the operation as it goes in the url ({@code .exec} for operations
     * with executor).
     */
    public String getOperationPath() {
        return operationPath;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    /**
     * @return the condition on the logged user ({@code user.<property>} or
     * {@code !user.<property>}), null when there is none.
     */
    public String getUserCondition() {
        return userCondition;
    }

    public JpmMenuItem withUserCondition(String userCondition) {
        this.userCondition = StringUtils.trimToNull(userCondition);
        return this;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * @return why the item can't be shown (unknown entity or operation, empty
     * url), null when it is valid.
     */
    public String getInvalidReason() {
        return invalidReason;
    }

    public boolean isValid() {
        return invalidReason == null;
    }

    public List<JpmMenuItem> getChildren() {
        return children;
    }
}
