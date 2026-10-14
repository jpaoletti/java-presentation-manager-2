package jpaoletti.jpm2.core.menu;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.service.executors.OperationExecutorSimple;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Builds the jstree JSON of the whole dynamic menu for the read-only preview.
 *
 * <p>Not a {@code @Component}: instantiated inline in the jpmMenu entity XML.
 *
 * @author jpaoletti
 */
public class JpmMenuPreviewExec extends OperationExecutorSimple {

    @Autowired
    private JpmMenuService service;

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        prepare.put("treeJson", service.treeJson());
        prepare.put("menuEnabled", service.isEnabled());
        return prepare;
    }
}
