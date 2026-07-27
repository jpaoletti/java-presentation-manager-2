package jpaoletti.jpm2.core.cache;

import java.util.Map;

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
    }, Redis {
        @Override
        public GeneralCache build(Map<String, String> cacheAdminParameters, String prefix) {
            return new GeneralCacheRedis(cacheAdminParameters, prefix);
        }
    };

    public abstract GeneralCache build(Map<String, String> cacheAdminParameters, String prefix);
}
