package jpaoletti.jpm2.core.cache;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.entityparam.EntityParameterDef;

/**
 * Cache backend factory. The enum ordinal is persisted (Map=0, Redis=1); do not
 * reorder the constants.
 *
 * @author jpaoletti
 */
public enum CacheType {

    Map {
        @Override
        public GeneralCache build(Map<String, String> cacheAdminParameters, String prefix) {
            return new GeneralCacheMap(cacheAdminParameters);
        }

        @Override
        public List<EntityParameterDef<?>> parameterDefs() {
            return Collections.emptyList();
        }
    }, Redis {
        @Override
        public GeneralCache build(Map<String, String> cacheAdminParameters, String prefix) {
            return new GeneralCacheRedis(cacheAdminParameters, prefix);
        }

        @Override
        public List<EntityParameterDef<?>> parameterDefs() {
            return GeneralCacheRedis.PARAMETER_DEFS;
        }
    };

    public abstract GeneralCache build(Map<String, String> cacheAdminParameters, String prefix);

    /** Parameter catalog of this backend (declared by its {@link GeneralCache} implementation). */
    public abstract List<EntityParameterDef<?>> parameterDefs();
}
