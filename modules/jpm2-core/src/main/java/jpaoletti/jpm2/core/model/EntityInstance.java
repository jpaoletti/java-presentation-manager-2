package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;

/**
 * Contains fields and fields values. It depends on operation.
 *
 * @author jpaoletti
 */
public class EntityInstance {

    private ContextualEntity contextualEntity;
    private IdentifiedObject iobject;
    private EntityInstanceOwner owner;
    private List<Operation> operations; //Individual operations
    private List<Field> fields;
    private Map<String, Object> values;
    private String highlight;

    /**
     * Used for lists.
     *
     * @param iobject
     * @param fields
     * @param ctx
     * @throws jpaoletti.jpm2.core.PMException
     */
    public EntityInstance(IdentifiedObject iobject, List<Field> fields, JPMContext ctx) throws PMException {
        this.iobject = iobject;
        this.values = new LinkedHashMap<>();
        this.fields = fields;
        this.highlight = "";
        contextualEntity = ctx.getContextualEntity();
        final Object object = (iobject != null) ? iobject.getObject() : null;
        final Entity entity = ctx.getEntity();
        if (object != null) {
            if (entity.isWeak(ctx.getEntityContext())) {
                final Object ownerobject = entity.getOwner(ctx.getEntityContext()).getOwnerObject(ctx.getEntityContext(), object);
                configureOwner(entity, ctx.getEntityContext(), ownerobject);
            }
            //configureItemOperations(entity, ctx.getEntityContext(), ctx.getOperation());
        }
        configureItemOperations(entity, ctx.getEntityContext(), ctx.getOperation());
    }

    /**
     * Used for single instance operations.
     *
     * @param ctx
     * @throws jpaoletti.jpm2.core.PMException
     */
    public EntityInstance(JPMContext ctx) throws PMException {
        this(null, ctx);
    }

    /**
     * Used for single instance operations.
     *
     * @param iobject
     * @param ctx
     * @throws jpaoletti.jpm2.core.PMException
     */
    public EntityInstance(IdentifiedObject iobject, JPMContext ctx) throws PMException {
        this(ctx.getContextualEntity(), iobject, ctx);
    }

    /**
     * Used for single instance operations.
     *
     * @param contextualEntity
     *
     * @param iobject
     * @param ctx
     * @throws jpaoletti.jpm2.core.PMException
     */
    public EntityInstance(ContextualEntity contextualEntity, IdentifiedObject iobject, JPMContext ctx) throws PMException {
        this.contextualEntity = contextualEntity;
        setIobject(iobject, ctx);
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Object getObject() {
        if (iobject == null) {
            return null;
        }
        return iobject.getObject();
    }

    public IdentifiedObject getIobject() {
        return iobject;
    }

    public void setIobject(IdentifiedObject iobject, JPMContext ctx) throws ConfigurationException, PMException {
        this.iobject = iobject;
        final Object object = (iobject != null) ? iobject.getObject() : null;
        final String id = (iobject != null) ? iobject.getId() : null;
        values = new LinkedHashMap<>();
        fields = new ArrayList<>();
        operations = new ArrayList<>();
        final Entity entity = contextualEntity.getEntity();
        if (object != null) {
            if (entity.isWeak(ctx.getEntityContext())) {
                final Object ownerobject = entity.getOwner(ctx.getEntityContext()).getOwnerObject(ctx.getEntityContext(), object);
                configureOwner(entity, ctx.getEntityContext(), ownerobject);
            }
        }
        for (Field field : entity.getOrderedFields(contextualEntity.getContext())) {
            if (field.shouldDisplay(ctx.getOperation().getId())) {
                final Converter converter = field.getConverter(this, ctx.getOperation());
                if (converter != null) {
                    try {
                        values.put(field.getId(), converter.visualize(
                                contextualEntity, field, object, id));
                        fields.add(field);
                    } catch (IgnoreConvertionException ex) {
                    }
                }
            }
        }
        if (object != null) {
            configureItemOperations(entity, ctx.getEntityContext(), ctx.getOperation());
        }
    }

    public EntityInstanceOwner getOwner() {
        return owner;
    }

    public void setOwner(EntityInstanceOwner owner) {
        this.owner = owner;
    }

    public String getOwnerId() throws NotAuthorizedException {
        if (getOwner().getIobject() == null && (contextualEntity == null || contextualEntity.getOwner() == null || !contextualEntity.getOwner().isOptional())) {
            throw new NotAuthorizedException();
        }
        if (getOwner().getIobject() == null) {
            return "";
        }
        return getOwner().getIobject().getId();
    }

    public String getId() {
        return (getIobject() != null) ? getIobject().getId() : "?";
    }

    protected final void configureItemOperations(Entity entity, String context, Operation operation) throws PMException {
        this.operations = entity.getOperationsFor(this, context, operation, OperationScope.ITEM);
    }

    public final void configureOwner(Entity entity, String context, final Object ownerobject) throws ConfigurationException {
        final Entity ownerEntity = entity.getOwner(context).getOwner();
        if (ownerobject != null) {
            final String ownerId = (entity.getOwner(context).isOnlyId()) ? ownerobject.toString() : String.valueOf(ownerEntity.getDao(context).getId(ownerobject));
            this.owner = new EntityInstanceOwner(ownerEntity, new IdentifiedObject(ownerId, ownerobject));
        } else {
            this.owner = new EntityInstanceOwner(ownerEntity, null);
        }
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
