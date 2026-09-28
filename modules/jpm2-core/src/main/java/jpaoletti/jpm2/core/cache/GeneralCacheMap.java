package jpaoletti.jpm2.core.cache;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * In-memory {@link GeneralCache} implementation backed by a {@link LinkedHashMap}
 * with lazy per-key expiration. Node-local (not shared across instances).
 * Thread-safe: every access is synchronized (operations are short and in
 * memory) so read-modify-write operations like incLong are atomic. Expired
 * keys are purged when read and periodically on writes.
 *
 * @author jpaoletti
 */
public class GeneralCacheMap extends GeneralCache {

    private final Map<String, String> cache = new LinkedHashMap<>();
    private final Map<String, Long> expirationMap = new LinkedHashMap<>();
    private static final int PURGE_EVERY = 500;
    private int writesSincePurge = 0;

    public GeneralCacheMap(Map<String, String> params) {
        super(params, "");
    }

    @Override
    public void printDebug() {
    }

    @Override
    public synchronized String get(String key) {
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
    public synchronized GeneralCacheMap set(String key, String value) {
        cache.put(key, value);
        if (++writesSincePurge >= PURGE_EVERY) {
            purgeExpired();
        }
        return this;
    }

    private void purgeExpired() {
        writesSincePurge = 0;
        final long now = System.currentTimeMillis();
        final Iterator<Map.Entry<String, Long>> it = expirationMap.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<String, Long> entry = it.next();
            if (entry.getValue() != null && now > entry.getValue()) {
                cache.remove(entry.getKey());
                it.remove();
            }
        }
    }

    @Override
    public synchronized void setExpiration(String key, long expirationMillis) {
        long expirationTime = System.currentTimeMillis() + expirationMillis;
        expirationMap.put(key, expirationTime);
    }

    @Override
    public synchronized BigDecimal incBigDecimal(String key, BigDecimal value) {
        String current = get(key);
        BigDecimal newValue
                = StringUtils.isEmpty(current) ? value : new BigDecimal(current).add(value);
        set(key, String.valueOf(newValue));
        return newValue;
    }

    @Override
    public synchronized long incLong(String key, long value) {
        String current = get(key);
        long newValue = StringUtils.isEmpty(current) ? value : Long.parseLong(current) + value;
        set(key, String.valueOf(newValue));
        return newValue;
    }

    @Override
    public synchronized int incInt(String key, int value) {
        String current = get(key);
        int newValue = StringUtils.isEmpty(current) ? value : Integer.parseInt(current) + value;
        set(key, String.valueOf(newValue));
        return newValue;
    }

    @Override
    public synchronized void clear() {
        cache.clear();
        expirationMap.clear();
    }

    @Override
    public synchronized void del(String key) {
        cache.remove(key);
        expirationMap.remove(key);
    }

    /**
     * @return a snapshot of the non expired entries
     */
    @Override
    public synchronized Map<String, String> getAll() {
        purgeExpired();
        return new LinkedHashMap<>(cache);
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
