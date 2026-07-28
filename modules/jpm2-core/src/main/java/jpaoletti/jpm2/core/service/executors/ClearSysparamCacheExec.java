package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.model.persistent.Sysparam;
import jpaoletti.jpm2.core.service.SysparamService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Clears the sysparam cache: the selected keys when invoked on items, or the whole region
 * when invoked as a general operation with no instances.
 *
 * <p>Not a {@code @Component}: instantiated inline in the sysparam entity XML.
 *
 * @author jpaoletti
 */
public class ClearSysparamCacheExec extends OperationExecutorSimple {

    @Autowired
    private SysparamService service;

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        if (instances == null || instances.isEmpty()) {
            service.clearCache();
        } else {
            for (EntityInstance instance : instances) {
                final Sysparam param = (Sysparam) instance.getIobject().getObject();
                service.clearCache(param.getKey());
            }
        }
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return true;
    }
}
