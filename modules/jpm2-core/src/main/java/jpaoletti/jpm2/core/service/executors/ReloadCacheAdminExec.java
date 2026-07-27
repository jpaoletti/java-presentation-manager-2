package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.model.persistent.CacheAdmin;
import jpaoletti.jpm2.core.service.CacheService;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Reloads the runtime cache registry for the selected {@link CacheAdmin} instances.
 *
 * <p>Not a {@code @Component}: it is instantiated inline in the cacheAdmin entity XML
 * so that only apps importing that entity require a {@code CacheService} bean. Inline
 * beans still receive {@code @Autowired} injection via the app's component scan.
 *
 * @author jpaoletti
 */
public class ReloadCacheAdminExec extends OperationExecutorSimple {

    @Autowired
    private CacheService service;

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        for (EntityInstance instance : instances) {
            final CacheAdmin c = (CacheAdmin) instance.getIobject().getObject();
            service.reload(c);
            getJpm().audit(ctx.getEntity(), ctx.getOperation(), instance.getIobject(), c.getDescription());
        }
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return true;
    }
}
