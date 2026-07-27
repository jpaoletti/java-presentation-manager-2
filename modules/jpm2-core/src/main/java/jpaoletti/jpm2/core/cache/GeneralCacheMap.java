package jpaoletti.jpm2.core.cache;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * In-memory {@link GeneralCache} implementation backed by a {@link LinkedHashMap}
 * with lazy per-key expiration. Node-local (not shared across instances).
 *
 * @author jpaoletti
 */
public class GeneralCacheMap extends GeneralCache {

    private final Map<String, String> cache = new LinkedHashMap<>();
    private final Map<String, Long> expirationMap = new LinkedHashMap<>();

    public GeneralCacheMap(Map<String, String> params) {
        super(params, "");
    }

    @Override
    public void printDebug() {
    }

    @Override
    public String get(String key) {
        if (!isExpired(key)) {
            return cache.get(key);
        } else {
            cache.remove(key);
            expirationMap.remove(key);
        }
        return null;
    }

    private boolean isExpired(String key) {
        Long expirationTime = expirationMap.get(key);
        return expirationTime != null && System.currentTimeMillis() > expirationTime;
    }

    @Override
    public GeneralCacheMap set(String key, String value) {
        cache.put(key, value);
        return this;
    }

    @Override
    public void setExpiration(String key, long expirationMillis) {
        long expirationTime = System.currentTimeMillis() + expirationMillis;
        expirationMap.put(key, expirationTime);
    }

    @Override
    public BigDecimal incBigDecimal(String key, BigDecimal value) {
        String current = get(key);
        BigDecimal newValue
                = StringUtils.isEmpty(current) ? value : new BigDecimal(current).add(value);
        set(key, String.valueOf(newValue));
        return newValue;
    }

    @Override
    public long incLong(String key, long value) {
        String current = get(key);
        long newValue = StringUtils.isEmpty(current) ? value : Long.parseLong(current) + value;
        set(key, String.valueOf(newValue));
        return newValue;
    }

    @Override
    public int incInt(String key, int value) {
        String current = get(key);
        int newValue = StringUtils.isEmpty(current) ? value : Integer.parseInt(current) + value;
        set(key, String.valueOf(newValue));
        return newValue;
    }

    @Override
    public void clear() {
        cache.clear();
        expirationMap.clear();
    }

    @Override
    public void del(String key) {
        cache.remove(key);
        expirationMap.remove(key);
    }

    @Override
    public Map<String, String> getAll() {
        return cache;
    }

    @Override
    public synchronized boolean setIfAbsent(String key, String value, long expirationMillis) {
        if (get(key) == null) {
            set(key, value);
            setExpiration(key, expirationMillis);
            return true;
        }
        return false;
    }
}
