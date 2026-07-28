package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.service.SysparamService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Builds the sysparam health report (catalog vs stored rows) for the admin health screen.
 * Read-only, general scope: it takes no instances and never mutates anything.
 *
 * <p>Not a {@code @Component}: instantiated inline in the sysparam entity XML.
 *
 * @author jpaoletti
 */
public class SysparamHealthExec extends OperationExecutorSimple {

    @Autowired
    private SysparamService service;

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        prepare.put("report", service.health());
        return prepare;
    }
}
