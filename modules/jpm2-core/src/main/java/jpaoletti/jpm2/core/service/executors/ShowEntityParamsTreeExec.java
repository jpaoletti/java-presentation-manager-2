package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.entityparam.EntityParameterTree;
import jpaoletti.jpm2.core.entityparam.ParameterizedEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;

/**
 * Item operation that renders a {@link ParameterizedEntity}'s parameters as a grouped tree (op-paramsTree.jsp),
 * with an inline popup to set each value — the entity-scoped analog of the Sysparam tree view. The child
 * entity id (for the per-leaf setValue URL) is provided as the {@code childEntity} bean property.
 *
 * <p>Not a {@code @Component}: instantiated inline in the parent entity XML, e.g.
 * {@code <bean class="...ShowEntityParamsTreeExec"><property name="childEntity" value="gatewayParameter"/></bean>}.
 *
 * @author jpaoletti
 */
public class ShowEntityParamsTreeExec extends OperationExecutorSimple {

    private String childEntity;

    public void setChildEntity(String childEntity) {
        this.childEntity = childEntity;
    }

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        for (EntityInstance instance : instances) {
            final Object object = instance.getIobject().getObject();
            if (object instanceof ParameterizedEntity) {
                prepare.put("treeJson", EntityParameterTree.json((ParameterizedEntity<?>) object));
            }
        }
        prepare.put("childEntity", childEntity);
        return prepare;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }
}
