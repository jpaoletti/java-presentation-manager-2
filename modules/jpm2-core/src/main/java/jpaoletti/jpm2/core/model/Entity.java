package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DAO;
import jpaoletti.jpm2.core.exception.FieldNotFoundException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.exception.OperationNotFoundException;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.BeanNameAware;

/**
 * An Entity is the visual representation and operation over a class of a data
 * model.
 *
 * @author jpaoletti
 *
 */
public class Entity extends PMCoreObject implements BeanNameAware {

    private String id; // Represents the entity id. This must me unique.
    private Integer numericId; //Optional reference id
    private String clazz;//The full name of the class represented by the entity.
    private DAO dao;
    private String order;
    private Entity parent;
    private boolean auditable;
    private EntityOwner owner;
    private boolean countable; //Enable the use of "count" on lists
    private boolean paginable; //Enable pagination on lists
    private String auth; //Needed authority to access any operation on this entity
    private String home; //default home if context one is not set.
    private List<Field> fields;
    private List<Entity> weaks;
    private List<PanelRow> panels;
    private List<Operation> operations;
    private Map<String, Field> fieldsbyid;
    private List<SearchDefinition> defaultSearchs;
    private String defaultSortField;
    private ListSort.SortDirection defaultSortDirection;
    private Integer pageSize;
    private Highlighter highlighter;
    private String auditId; //Optional ID for auditoring

    private List<EntityContext> contexts;

    public Entity() {
        super();
        this.fieldsbyid = null;
        this.paginable = true;
        this.countable = true;
        this.auditable = true;
        this.pageSize = 10;
        this.contexts = new ArrayList<>();
    }

    /**
     * Return the list of fields including inherited ones.
     *
     * @return The list
     */
    public List<Field> getAllFields(String context) {
        final List<Field> r = new ArrayList<>();
        final List<String> ids = new ArrayList<>();
        if (getFields() != null) {
            for (Field field : getFields()) {
                r.add(field);
                ids.add(field.getId());
            }
        }
        if (getParent() != null) {
            for (Field field : getParent().getAllFields(context)) {
                if (!ids.contains(field.getId())) {
                    r.add(field);
                }
            }
        }
        final EntityContext ctx = getContext(context);
        if (ctx != null && ctx.getFields() != null && !ctx.getFields().isEmpty()) {
            for (Field field : ctx.getFields()) {
                if (!ids.contains(field.getId())) {
                    r.add(field);
                }
            }
        }
        return r;
    }

    /**
     * Getter for a field by its id
     *
     * @param id The Field id
     * @return The Field with the given id
     * @throws jpaoletti.jpm2.core.exception.FieldNotFoundException when the
     * field was not found for this entity
     */
    public Field getFieldById(String id, String context) throws FieldNotFoundException {
        final Field res = getFieldsbyid(context).get(id);
        if (res == null) {
            throw new FieldNotFoundException(getId(), id);
        }
        return res;
    }

    /**
     * Getter for fieldsbyid. If its null, this methods fill it
     *
     * @return The mapped field list
     *
     */
    private Map<String, Field> getFieldsbyid(String context) {
        if (fieldsbyid == null) {
            fieldsbyid = new HashMap<>();
            for (Field f : getAllFields(context)) {
                fieldsbyid.put(f.getId(), f);
            }
        }
        return fieldsbyid;
    }

    /**
     * This method sorts the fields and returns them
     *
     * @return fields ordered
     *
     */
    public List<Field> getOrderedFields(String context) {
        try {
            if (isOrdered()) {
                final ArrayList<Field> r = new ArrayList<>(getAllFields(context));
                Collections.sort(r, new FieldComparator(getOrder()));
                return r;
            }
        } catch (Exception e) {
            JPMUtils.getLogger().error(e);
        }
        return getAllFields(context);
    }

    /**
     * Determine if the entity have the order property
     *
     * @return true if order != null
     */
    public boolean isOrdered() {
        return getOrder() != null;
    }

    /**
     * String representation of an entity
     *
     * @return The string
     */
    @Override
    public String toString() {
        return "Entity (" + id + ") " + clazz;
    }

    /**
     * Getter for id
     *
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
     * Getter for clazz
     *
     * @return the clazz
     */
    public String getClazz() {
        if (clazz != null) {
            return clazz;
        } else if (getParent() != null) {
            return getParent().getClazz();
        } else {
            return null;
        }
    }

    /**
     * @param clazz the clazz to set
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    /**
     * Getter for order
     *
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * Indicates if the entity is auditable or not
     *
     * @return the auditable
     */
    public boolean isAuditable() {
        return auditable;
    }

    /**
     * @param auditable the auditable to set
     */
    public void setAuditable(boolean auditable) {
        this.auditable = auditable;
    }

    /**
     * Getter for owner. Use the one with context!
     *
     * @return the owner
     */
    public EntityOwner getOwner() {
        return owner;
    }

    public EntityOwner getOwner(String context) {
        final EntityContext ctx = getContext(context);
        if (ctx != null) {
            return ctx.getOwner();
        }
        return getOwner();
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(EntityOwner owner) {
        this.owner = owner;
    }

    /**
     * Getter for entity fields
     *
     * @return the fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public List<Operation> getAllOperations() {
        if (getParent() == null) {
            return getOperations();
        } else {
            final List<Operation> res = new ArrayList<>();
            for (Operation operation : getParent().getAllOperations()) {
                if (getOperations() == null || !getOperations().contains(operation)) {
                    res.add(operation);
                }
            }
            if (getOperations() != null) {
                res.addAll(getOperations());
            }
            return res;
        }
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public Highlighter getHighlighter() {
        return highlighter;
    }

    public void setHighlighter(Highlighter highlighter) {
        this.highlighter = highlighter;
    }

    /**
     * @param weaks the weaks to set
     */
    public void setWeaks(List<Entity> weaks) {
        this.weaks = weaks;
    }

    /**
     * @return the weaks
     */
    public List<Entity> getWeaks() {
        return weaks;
    }

    /**
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Compares two entities by id to check if they are equals
     *
     * @param obj
     * @return true if both are the same entity
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public boolean isCountable() {
        return countable;
    }

    public void setCountable(boolean countable) {
        this.countable = countable;
    }

    /**
     *
     * @return true if the entity is weak
     */
    public boolean isWeak() {
        return getOwner() != null;
    }

    public boolean isWeak(String context) {
        return getOwner(context) != null;
    }

    /**
     * Returns the entity title key.
     *
     * @return
     */
    public String getTitle() {
        final String key = String.format("jpm.entity.title.%s", getId());
        final String title = getMessage(key);
        if (title.equals(key) && getParent() != null) {
            return getParent().getTitle();
        }
        return title;
    }

    public List<PanelRow> getPanels() {
        if (panels != null) {
            return panels;
        } else if (getParent() != null) {
            return getParent().getPanels();
        } else {
            return null;
        }
    }

    public void setPanels(List<PanelRow> panels) {
        this.panels = panels;
    }

    public Entity getParent() {
        return parent;
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public DAO getDao() {
        return getDao(null);
    }

    public DAO getDao(String context) {
        final EntityContext ctx = getContext(context);
        if (ctx != null && ctx.getDao() != null) {
            return ctx.getDao();
        }
        if (dao != null) {
            return dao;
        } else if (getParent() != null) {
            return getParent().getDao(context);
        }
        return null;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the operation of the given id or a new default operation. If no
     * operation was found, OperationNotFoundException is trhown.
     *
     * @param id The id
     * @param context
     * @return The operation
     * @throws jpaoletti.jpm2.core.exception.OperationNotFoundException when
     * operation does not exists for this entity.
     * @throws jpaoletti.jpm2.core.exception.NotAuthorizedException when the
     * operation exists but the user has no permission to access it.
     */
    public Operation getOperation(String id, EntityContext context) throws OperationNotFoundException, NotAuthorizedException {
        for (Operation oper : getAllOperations()) {
            if (oper.getId().compareTo(id) == 0) {
                oper.checkAuthorization(this, context);
                return oper;
            }
        }
        throw new OperationNotFoundException(getId(), id);
    }

    public List<Operation> getOperationsFor(EntityInstance instance, String context, Operation operation, OperationScope... scopes) throws PMException {
        final List<OperationScope> _scopes = Arrays.asList(scopes);
        final List<Operation> r = new ArrayList<>();
        if (operation != null) {
            //IF
            for (Operation op : getAllOperations()) {
                //Operation is displayed and enabled and no the same we are checking
                if (op.isDisplayed(operation.getId()) && op.isEnabled() && !op.equals(operation)) {
                    //User has role
                    try {
                        op.checkAuthorization(this, getContext(context));
                        //Scope is adecuate
                        if (_scopes.contains(op.getScope())) {
                            //Conditions are ok
                            if (op.getCondition() == null || op.getCondition().check(instance, op, operation.getId())) {
                                //the we add the operation to list.
                                //TODO CHECK CONTEXT
                                r.add(op);
                            }
                        }
                    } catch (NotAuthorizedException ignoreme) {
                    }
                }
            }
        }
        return r;
    }

    @Override
    public void setBeanName(String string) {
        this.id = string;
    }

    public boolean isPaginable() {
        return paginable;
    }

    public void setPaginable(boolean paginable) {
        this.paginable = paginable;
    }

    @Override
    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getHome() {
        return home;
    }

    public String getHome(String context) {
        final EntityContext ctx = getContext(context);
        if (ctx != null && ctx.getHome() != null) {
            return ctx.getHome();
        }
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public List<SearchDefinition> getDefaultSearchs() {
        return defaultSearchs;
    }

    public void setDefaultSearchs(List<SearchDefinition> defaultSearchs) {
        this.defaultSearchs = defaultSearchs;
    }

    public String getDefaultSortField(String context) {
        final EntityContext ctx = getContext(context);
        if (ctx != null && ctx.getDefaultSortField() != null) {
            return ctx.getDefaultSortField();
        }
        if (defaultSortField != null) {
            return defaultSortField;
        } else if (getParent() != null) {
            return getParent().getDefaultSortField(context);
        } else {
            return null;
        }
    }

    public void setDefaultSortField(String defaultSortField) {
        this.defaultSortField = defaultSortField;
    }

    public ListSort.SortDirection getDefaultSortDirection(String context) {
        final EntityContext ctx = getContext(context);
        if (ctx != null && ctx.getDefaultSortDirection() != null) {
            return ctx.getDefaultSortDirection();
        }
        if (defaultSortDirection != null) {
            return defaultSortDirection;
        } else if (getParent() != null) {
            return getParent().getDefaultSortDirection(context);
        } else {
            return null;
        }
    }

    public void setDefaultSortDirection(ListSort.SortDirection defaultSortDirection) {
        this.defaultSortDirection = defaultSortDirection;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getNumericId() {
        return numericId;
    }

    public void setNumericId(Integer numericId) {
        this.numericId = numericId;
    }

    public String getAuditId() {
        if (auditId == null) {
            return getId();
        } else {
            return auditId;
        }
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public boolean isContainingListOperation() {
        try {
            return getOperation("list", null) != null; //cant use context here
        } catch (OperationNotFoundException | NotAuthorizedException ex) {
            return false;
        }
    }

    public List<EntityContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<EntityContext> contexts) {
        this.contexts = contexts;
    }

    public EntityContext getContext(String id) {
        if (id != null) {
            for (EntityContext entityContext : getContexts()) {
                if (id.equals(entityContext.getId())) {
                    return entityContext;
                }
            }
        }
        return null;
    }

    public String getAuthKey(EntityContext context) {
        return "jpm.auth.entity." + getId() + (context == null ? "" : "." + context.getId());
    }

    public List<Field> getFieldsByOperation(String id) {
        return getFields().stream().filter(f -> f.shouldDisplay(id)).collect(Collectors.toList());
    }
}
