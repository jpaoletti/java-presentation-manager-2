package jpaoletti.jpm2.web.executors;

import jpaoletti.jpm2.core.cache.GeneralCache;
import jpaoletti.jpm2.core.model.persistent.CacheAdmin;
import jpaoletti.jpm2.core.service.CacheService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.service.executors.OperationExecutorSimple;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Queries and manually deposits the value of a single key in the cache region
 * associated with a {@link CacheAdmin}.
 * <ul>
 * <li>GET with {@code ?key=...}: reads the current value of the key and repopulates
 * the form (does not modify the cache).</li>
 * <li>POST with {@code key} and {@code value}: deposits the value into the cache.</li>
 * </ul>
 *
 * <p>Not a {@code @Component}: instantiated inline in the cacheAdmin entity XML. It
 * lives in the web module because it reads the request query string on {@code prepare}.
 *
 * @author jpaoletti
 */
public class CacheEntryExec extends OperationExecutorSimple {

    @Autowired
    private CacheService service;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        final String key = StringUtils.trimToNull(request.getParameter("key"));
        for (EntityInstance instance : instances) {
            final CacheAdmin cache = (CacheAdmin) instance.getIobject().getObject();
            prepare.put("cacheId", cache.getId());
            prepare.put("code", cache.getCode());
            if (key != null) {
                final String value = service.getCache(cache.getCode()).get(key);
                prepare.put("queried", true);
                prepare.put("selectedKey", key);
                prepare.put("selectedValue", value);
                prepare.put("found", value != null);
            }
        }
        return prepare;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        final String key = StringUtils.trimToNull(getSimpleParameterValue(parameters, "key"));
        final String value = getSimpleParameterValue(parameters, "value");
        if (key == null) {
            throw new PMException("jpm.cacheEntry.emptyKey");
        }
        for (EntityInstance instance : instances) {
            final CacheAdmin cache = (CacheAdmin) instance.getIobject().getObject();
            final GeneralCache generalCache = service.getCache(cache.getCode());
            generalCache.set(key, value == null ? "" : value);
            return "/jpm/cacheAdmin/" + cache.getId() + "/cacheEntry.exec?key=" + key;
        }
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }
}
