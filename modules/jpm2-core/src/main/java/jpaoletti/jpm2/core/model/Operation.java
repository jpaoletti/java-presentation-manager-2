package jpaoletti.jpm2.core.model;

import java.util.Objects;
import java.util.Properties;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;

/**
 * An Operation is any action that can be applied to an Entity or to an Entity
 * Instance. Most common operations are list, add, delete, edit and show but
 * programmer can define any new. To give it a new style or icon there must be a
 * css class defined on your template/buttons.css with the class name equals to
 * operation id. The title of the operation is determined by an entry in the
 * ApplicationResource file with the key "operation._op_id_"
 *
 * @author jpaoletti
 *
 */
public class Operation extends PMCoreObject {

    public static final String ROLE_SPECIAL = "SPECIAL";

    //The operation Id. Must be unique and only one word
    private String id;
    private String operation;
    //Determine if the operation is enabled or not.
    private boolean enabled;
    private OperationScope scope;
    /**
     * A String with other operations id separated by blanks where this
     * operation will be shown
     */
    private String display;
    //If defined, its a direct link to a fixed URL
    private String url;
    //Indicates if the entity's title is shown
    private boolean showTitle;
    //Indicate if a confirmation is needed before proceed.
    private boolean confirm;
    private OperationContext context;
    private OperationValidator validator;
    //A properties object to get some extra configurations
    private Properties properties;
    // Another operation ID that follows this one on success
    private String follows;
    //Conditional to show on others
    private OperationCondition condition;
    private boolean available;
    //Display a compact visual representation, usually an icon without text
    private boolean compact; //Default: false
    //Display operation in a "popup" visualization instead of redirecting it
    private boolean popup; //Default: false
    private boolean navigable; //Default: true, if navigable, impacts on NavigationList
    private boolean auditable; //Default: true, if navigable, impacts on NavigationList
    private String auth; //Needed authority to access any operation on this entity
    private boolean repeatable;
    private boolean useFields;

    private OperationExecutor executor = null;
    private boolean synchronic;

    public Operation() {
        this.enabled = true;
        this.showTitle = true;
        this.compact = false;
        this.popup = false;
        this.available = true;
        this.confirm = false;
        this.navigable = true;
        this.auditable = true;
        this.repeatable = false;
        this.useFields = true;
        this.synchronic = true;
    }

    public Operation(String opId) {
        this();
        this.operation = opId;
    }

    public String getPathId() {
        return getId() + (getExecutor() == null ? "" : ".exec");
    }

    public void checkAuthorization(Entity entity, EntityContext context) throws NotAuthorizedException {
        if (getAuth() != null) {
            checkAuthorization();//getAuthorizationService().getCurrentUsername();
        } else if (!getAuthorizationService().userHasRole(ROLE_SPECIAL)) {
            final String authKey = getAuthKey(entity, context);
            //if user is "special" then we ignore the new auth system
            if (!getAuthorizationService().userHasRole(authKey)) {
                throw new NotAuthorizedException(authKey);
            }
        }
    }

    public OperationCondition getCondition() {
        return condition;
    }

    public void setCondition(OperationCondition condition) {
        this.condition = condition;
    }

    public String getFollows() {
        return follows;
    }

    public void setFollows(String follows) {
        this.follows = follows;
    }

    /**
     * Determine if this operation is visible in another.
     *
     * @param other The id of the other operation
     * @return true if this operation is visible in the other
     */
    public boolean isDisplayed(String other) {
        return ((getDisplay() == null || getDisplay().contains("all") || getDisplay().contains(other)))
                && (!getDisplay().contains("!" + other)); //new, this is for ignoring operations, like "all !add" means 'all' but not 'add'
    }

    /**
     * Redefines toString from object
     *
     * @return
     */
    @Override
    public String toString() {
        return getId();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the scope
     */
    public OperationScope getScope() {
        if (scope == null) {
            return OperationScope.ITEM;
        }
        return scope;
    }

    public void setScope(OperationScope scope) {
        this.scope = scope;
    }

    /**
     * @return the display
     */
    public String getDisplay() {
        if (display == null || display.trim().compareTo("") == 0) {
            return "all";
        }
        return display;
    }

    /**
     * @param display the display to set
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the context
     */
    public OperationContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(OperationContext context) {
        this.context = context;
    }

    /**
     * Getter for a specific property with a default value in case its not
     * defined. Only works for string.
     *
     * @param name Property name
     * @param def Default value
     * @return Property value only if its a string
     */
    public String getConfig(String name, String def) {
        if (properties != null) {
            Object obj = properties.get(name);
            if (obj instanceof String) {
                return obj.toString();
            }
        }
        return def;
    }

    /**
     * Getter for any property in the properties object
     *
     * @param name The property name
     * @return The property value
     */
    public String getConfig(String name) {
        return getConfig(name, null);
    }

    /**
     *
     * @param showTitle
     */
    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    /**
     * @param confirm the confirm to set
     */
    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    @Override
    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    /**
     * Returns the internationalized operation title
     *
     * @return
     */
    public String getTitle() {
        return "jpm.operation." + getId();
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    public void setPopup(boolean popup) {
        this.popup = popup;
    }

    public boolean isAuditable() {
        return auditable;
    }

    public void setAuditable(boolean auditable) {
        this.auditable = auditable;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isCompact() {
        return compact;
    }

    public boolean isPopup() {
        return popup;
    }

    public boolean isNavigable() {
        return navigable;
    }

    public void setNavigable(boolean navigable) {
        this.navigable = navigable;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getProperty(String name, String def) {
        if (getProperties() == null || getProperties().isEmpty()) {
            return def;
        } else {
            return getProperties().getProperty(name, def);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public OperationValidator getValidator() {
        return validator;
    }

    public void setValidator(OperationValidator validator) {
        this.validator = validator;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Operation other = (Operation) obj;
        return Objects.equals(this.id, other.id);
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public String getOperation() {
        if (operation == null) {
            return getId();
        }
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public boolean isUseFields() {
        return useFields;
    }

    public void setUseFields(boolean useFields) {
        this.useFields = useFields;
    }

    public String getAuthKey(Entity entity, EntityContext context) {
        final String eid = entity.getId() + (context == null ? "" : "." + context.getId());
        return String.format("jpm.auth.operation.%s.%s", eid, getId());
    }

    public OperationExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(OperationExecutor executor) {
        this.executor = executor;
    }

    public boolean isSynchronic() {
        return synchronic;
    }

    public void setSynchronic(boolean synchronic) {
        this.synchronic = synchronic;
    }
}
