package jpaoletti.jpm2.core.cache;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Abstract cache region. A region is a named key/value store backed by an
 * in-memory map or by Redis (see {@link CacheType}). Domain-specific region
 * names are NOT declared here; each application defines its own constants.
 *
 * @author jpaoletti
 */
public abstract class GeneralCache {

    public GeneralCache(Map<String, String> params, String prefix) {
    }

    public abstract String get(String key);

    public abstract GeneralCache set(String key, String value);

    /**
     * Sets in how many milliseconds the key stops being valid.
     *
     * @param key
     * @param expirationMillis
     */
    public abstract void setExpiration(String key, long expirationMillis);

    /**
     * Sets an expiration date for the key validity.
     *
     * @param key
     * @param expiration
     */
    public void setExpiration(String key, Date expiration) {
        setExpiration(key, expiration.getTime() - new Date().getTime());
    }

    public abstract BigDecimal incBigDecimal(String key, BigDecimal value);

    public abstract long incLong(String key, long value);

    public abstract int incInt(String key, int value);

    public abstract void printDebug();

    public abstract void clear();

    public abstract void del(String key);

    public abstract Map<String, String> getAll();

    /**
     * Sets a value only if the key does not exist (atomic operation). Useful for
     * distributed locks.
     *
     * @param key key
     * @param value value
     * @param expirationMillis expiration time in milliseconds
     * @return true if the value was set (key did not exist), false if it already existed
     */
    public abstract boolean setIfAbsent(String key, String value, long expirationMillis);
}
