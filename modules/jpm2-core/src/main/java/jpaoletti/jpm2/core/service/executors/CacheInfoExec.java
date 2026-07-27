package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.cache.GeneralCache;
import jpaoletti.jpm2.core.model.persistent.CacheAdmin;
import jpaoletti.jpm2.core.service.CacheService;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Dumps every entry of the cache region owned by a {@link CacheAdmin} for display.
 *
 * <p>Not a {@code @Component}: instantiated inline in the cacheAdmin entity XML.
 *
 * @author jpaoletti
 */
public class CacheInfoExec extends OperationExecutorSimple {

    @Autowired
    private CacheService service;

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        for (EntityInstance instance : instances) {
            final CacheAdmin cache = (CacheAdmin) instance.getIobject().getObject();
            final GeneralCache generalCache = service.getCache(cache.getCode());
            prepare.put("info", generalCache.getAll());
        }
        return prepare;
    }
}
