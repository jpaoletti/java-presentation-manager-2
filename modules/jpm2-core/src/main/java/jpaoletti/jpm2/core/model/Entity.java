package jpaoletti.jpm2.core.model;

import java.util.*;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.GenericDAO;
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
    private String clazz;//The full name of the class represented by the entity.
    private GenericDAO dao;
    private String order;
    private Entity parent;
    private boolean auditable;
    private EntityOwner owner;
    private Highlights highlights;
    private boolean countable; //Enable the use of "count" on lists
    private boolean paginable; //Enable pagination on lists
    private String auth; //Needed authority to access any operation on this entity
    private String home; //default home if context one is not set.
    private List<Field> fields;
    private List<Entity> weaks;
    private List<PanelRow> panels;
    private List<Operation> operations;
    private Map<String, Field> fieldsbyid;

    public Entity() {
        super();
        this.fieldsbyid = null;
        this.paginable = true;
        this.countable = true;
        this.auditable = true;
        this.home = "index";
    }

    /**
     * Return the list of fields including inherited ones.
     *
     * @return The list
     */
    public List<Field> getAllFields() {
        final List<Field> r = new ArrayList<>();
        final List<String> ids = new ArrayList<>();
        if (getFields() != null) {
            for (Field field : getFields()) {
                r.add(field);
                ids.add(field.getId());
            }
        }
        if (getParent() != null) {
            for (Field field : getParent().getAllFields()) {
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
     */
    public Field getFieldById(String id) {
        return getFieldsbyid().get(id);
    }

    /**
     * Getter for fieldsbyid. If its null, this methods fill it
     *
     * @return The mapped field list
     *
     */
    private Map<String, Field> getFieldsbyid() {
        if (fieldsbyid == null) {
            fieldsbyid = new HashMap<>();
            for (Field f : getAllFields()) {
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
    public List<Field> getOrderedFields() {
        try {
            if (isOrdered()) {
                ArrayList<Field> r = new ArrayList<>(getAllFields());
                Collections.sort(r, new FieldComparator(getOrder()));
                return r;
            }
        } catch (Exception e) {
            JPMUtils.getLogger().error(e);
        }
        return getAllFields();
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
        return clazz;
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
     * Getter for owner
     *
     * @return the owner
     */
    public EntityOwner getOwner() {
        return owner;
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
            if (getOperations() != null) {
                res.addAll(getOperations());
            }
            for (Operation operation : getParent().getAllOperations()) {
                if (!res.contains(operation)) {
                    res.add(operation);
                }
            }
            return res;
        }
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    /**
     * @param highlights the highlights to set
     */
    public void setHighlights(Highlights highlights) {
        this.highlights = highlights;
    }

    /**
     * @return the highlights
     */
    public Highlights getHighlights() {
        return highlights;
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
     * Looks for an apropiate highlight for this field+instance
     *
     * @param field
     * @param instance
     * @return the highlight
     */
    public Highlight getHighlight(Field field, Object instance) {
        if (getHighlights() == null) {
            return null;
        }
        return getHighlights().getHighlight(this, field, instance);
    }

    /**
     * Looks for all the apropiate highlight for this field+instance
     *
     * @param field
     * @param instance
     * @return the highlight
     */
    public List<Highlight> getHighlights(Field field, Object instance) {
        if (getHighlights() == null) {
            return null;
        }
        return getHighlights().getHighlights(this, field, instance);
    }

    /**
     *
     * @return true if the entity is weak
     */
    public boolean isWeak() {
        return getOwner() != null;
    }

    /**
     * Returns the entity title key.
     */
    public String getTitle() {
        return getMessage(String.format("jpm.entity.title.%s", getId()));
    }

    /**
     * @return true if the entity has operations with selected scope
     */
    public boolean hasSelectedScopeOperations() {
        if (getOperations() != null) {
            return getOperationsForScope(OperationScope.SELECTED).size() > 0;
        } else {
            return false;
        }
    }

    public List<PanelRow> getPanels() {
        return panels;
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

    public GenericDAO getDao() {
        return dao;
    }

    public void setDao(GenericDAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the operation of the given id or a new default operation. If no
     * operation was found, null is returned.
     *
     * @param id The id
     * @return The operation
     */
    public Operation getOperation(String id) {
        for (Iterator<Operation> it = getAllOperations().iterator(); it.hasNext();) {
            Operation oper = it.next();
            if (oper.getId().compareTo(id) == 0) {
                return oper;
            }
        }
        return null;
    }

    public List<Operation> getItemOperations() {
        return getOperationsForScope(OperationScope.ITEM);
    }

    public List<Operation> getGeneralOperations() {
        return getOperationsForScope(OperationScope.GENERAL);
    }

    /**
     * Returns an Operations object for the given scope
     *
     * @param scopes The scopes
     * @return The Operations
     */
    public List<Operation> getOperationsForScope(OperationScope... scopes) {
        final List<Operation> r = new ArrayList<>();
        if (getAllOperations() != null) {
            for (Operation op : getAllOperations()) {
                if (op.getScope() != null) {
                    String s = op.getScope().trim();
                    for (int i = 0; i < scopes.length; i++) {
                        OperationScope scope = scopes[i];
                        if (scope.is(s)) {
                            r.add(op);
                            break;
                        }
                    }
                }
            }
        }
        return r;
    }

    public List<Operation> getOperationsFor(Object object, Operation operation, OperationScope scope) throws PMException {
        final List<Operation> r = new ArrayList<>();
        if (operation != null) {
            for (Operation op : getAllOperations()) {
                if (op.isDisplayed(operation.getId()) && op.isEnabled() && !op.equals(operation)) {
                    if (op.getCondition() == null || op.getCondition().check(object, op, operation.getId())) {
                        if (scope.is(op.getScope())) {
                            r.add(op);
                        }
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

    public void setHome(String home) {
        this.home = home;
    }
}
