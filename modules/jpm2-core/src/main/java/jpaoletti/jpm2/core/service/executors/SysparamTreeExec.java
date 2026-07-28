package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.service.SysparamService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Builds the jstree JSON of the whole sysparam catalog (groups -&gt; parameters) for the
 * read-only tree view. Read-only, general scope: it takes no instances and never mutates.
 *
 * <p>Not a {@code @Component}: instantiated inline in the sysparam entity XML.
 *
 * @author jpaoletti
 */
public class SysparamTreeExec extends OperationExecutorSimple {

    @Autowired
    private SysparamService service;

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        prepare.put("treeJson", service.treeJson());
        return prepare;
    }
}
