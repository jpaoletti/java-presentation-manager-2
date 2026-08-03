package jpaoletti.jpm2.core.cache;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jpaoletti.jpm2.core.entityparam.EntityParameterDef;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.resps.ScanResult;

/**
 * Redis-backed {@link GeneralCache} implementation using a Jedis pool. Keys are
 * namespaced with a global prefix plus the region prefix.
 *
 * @author jpaoletti
 */
public final class GeneralCacheRedis extends GeneralCache {

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int DEFAULT_MAX_TOTAL = 32;
    private static final int DEFAULT_MAX_WAIT_MILLIS = 3000;

    /**
     * Parameters this backend reads from its
     * {@link jpaoletti.jpm2.core.model.persistent.CacheAdmin} (catalog kind
     * {@code "cache-admin"}), co-located with the code that consumes them.
     * {@code password} is secret (encrypted at rest, masked); defaults match
     * the {@code getOrDefault(...)} calls below. Surfaced to the owner through
     * {@link CacheType#parameterDefs()} so no backend instance (Jedis pool) is
     * created just to read the catalog. Types deduced; adjust as needed.
     */
    public static final List<EntityParameterDef<?>> PARAMETER_DEFS = Arrays.<EntityParameterDef<?>>asList(
            EntityParameterDef.secret("cache-admin", "password").group("credentials").build(),
            EntityParameterDef.string("cache-admin", "host").group("redis").defRaw("localhost").build(),
            EntityParameterDef.integer("cache-admin", "port").group("redis").defRaw("6379").build(),
            EntityParameterDef.string("cache-admin", "username").group("redis").build(),
            EntityParameterDef.string("cache-admin", "prefix").group("redis").build(),
            EntityParameterDef.integer("cache-admin", "timeout").group("redis").defRaw("5000").build(),
            EntityParameterDef.integer("cache-admin", "max-total").group("pool").defRaw("32").build(),
            EntityParameterDef.integer("cache-admin", "max-idle").group("pool").defRaw("32").build(),
            EntityParameterDef.integer("cache-admin", "min-idle").group("pool").defRaw("0").build(),
            EntityParameterDef.integer("cache-admin", "max-wait-millis").group("pool").defRaw("3000").build()
    );

    private final JedisPool pool;
    private final String prefix;
    private String redisPrefix = "";

    private String getWithPrefix(String value) {
        return redisPrefix + this.prefix + "-" + value;
    }

    public GeneralCacheRedis(Map<String, String> params, String prefix) {
        super(params, prefix);
        String host = params.getOrDefault("host", "localhost");
        int port = Integer.parseInt(params.getOrDefault("port", "6379"));
        String user = params.getOrDefault("user", params.getOrDefault("username", ""));
        redisPrefix = params.getOrDefault("prefix", "");
        String password = params.getOrDefault("password", "");
        String pass = StringUtils.isEmpty(password) ? null : password;
        final int timeout = intParam(params, "timeout", DEFAULT_TIMEOUT);
        final JedisPoolConfig config = buildPoolConfig(params);
        if (StringUtils.isEmpty(user)) {
            // No user: AUTH with password only -> authenticates as the "default" user
            pool = new JedisPool(config, host, port, timeout, pass);
        } else {
            // Redis 6+ ACL: AUTH <user> <password>
            pool = new JedisPool(config, host, port, timeout, user, pass);
        }
        this.prefix = prefix;
    }

    /**
     * Reads an optional integer parameter, falling back to {@code defaultValue}
     * when it is absent, blank or not a number. A typo in the cache
     * administration screen must not leave the region unusable.
     */
    private static int intParam(Map<String, String> params, String name, int defaultValue) {
        final String value = params.get(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            JPMUtils.getLogger().warn("invalid redis cache parameter " + name + "=" + value + ", using " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Pool configuration, overridable per region with the {@code max-total},
     * {@code max-idle}, {@code min-idle}, {@code max-wait-millis} and
     * {@code timeout} cache administration parameters.
     *
     * <p>
     * CORE-1113: the defaults of {@link JedisPoolConfig} are deliberately not
     * used: they allow only 8 connections and wait forever for one to become
     * available. With that combination any connection leak or unresponsive
     * Redis instance parks every caller indefinitely, which is exactly how a
     * whole application ends up with all of its request threads stuck in
     * {@code JedisPool.getResource()} and no error logged anywhere. A finite
     * {@code maxWait} turns that permanent freeze into a plain exception per
     * call.
     *
     * <p>
     * Note that callers therefore have to expect a {@code JedisException} when
     * the pool is exhausted, where before they simply blocked.
     */
    private static JedisPoolConfig buildPoolConfig(Map<String, String> params) {
        final JedisPoolConfig config = new JedisPoolConfig();
        final int maxTotal = intParam(params, "max-total", DEFAULT_MAX_TOTAL);
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(intParam(params, "max-idle", maxTotal));
        config.setMinIdle(intParam(params, "min-idle", 0));
        config.setBlockWhenExhausted(true);
        config.setMaxWait(Duration.ofMillis(intParam(params, "max-wait-millis", DEFAULT_MAX_WAIT_MILLIS)));
        // Drop connections the server closed on its side instead of handing them out
        config.setTestWhileIdle(true);
        config.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        return config;
    }

    @Override
    public String get(String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(getWithPrefix(key));
        } catch (Exception ex) {
            JPMUtils.getLogger().error("error en redis get", ex);
            return null;
        }
    }

    @Override
    public GeneralCacheRedis set(String key, String value) {
        try (Jedis jedis = pool.getResource()) {
            jedis.set(getWithPrefix(key), value != null ? value : "");
            return this;
        }
    }

    @Override
    public void setExpiration(String key, long expirationMillis) {
        try (Jedis jedis = pool.getResource()) {
            jedis.expire(getWithPrefix(key), (int) (expirationMillis / 1000L));
        }
    }

    @Override
    public BigDecimal incBigDecimal(String key, BigDecimal value) {
        try (Jedis jedis = pool.getResource()) {
            return BigDecimal.valueOf(jedis.incrByFloat(getWithPrefix(key), value.doubleValue()));
        }
    }

    @Override
    public long incLong(String key, long value) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.incrBy(getWithPrefix(key), value);
        }
    }

    @Override
    public int incInt(String key, int value) {
        try (Jedis jedis = pool.getResource()) {
            return Math.toIntExact(jedis.incrBy(getWithPrefix(key), value));
        }
    }

    @Override
    public void printDebug() {
    }

    /**
     * Every key of this region, as stored in Redis (prefix included).
     *
     * <p>
     * Uses SCAN instead of KEYS: KEYS walks the whole keyspace in a single
     * blocking call, which on a shared production instance stalls every other
     * client.
     *
     * <p>
     * Beware that the match pattern is {@code <prefix>-*}, so a region whose
     * code is a prefix of another region's code also sees the sibling's keys.
     * For example region {@code token-auth} matches the keys of
     * {@code token-auth-transfer-out}.
     */
    private Set<String> scanKeys(Jedis jedis) {
        final Set<String> res = new LinkedHashSet<>();
        final ScanParams params = new ScanParams().match(getWithPrefix("*")).count(500);
        String cursor = ScanParams.SCAN_POINTER_START;
        do {
            final ScanResult<String> scan = jedis.scan(cursor, params);
            res.addAll(scan.getResult());
            cursor = scan.getCursor();
        } while (!ScanParams.SCAN_POINTER_START.equals(cursor));
        return res;
    }

    /**
     * Removes every key of this region.
     *
     * <p>
     * WARNING: see {@link #scanKeys(Jedis)}. Clearing a region whose code is a
     * prefix of another region's code also wipes the sibling region.
     */
    @Override
    public void clear() {
        try (Jedis jedis = pool.getResource()) {
            final Set<String> keys = scanKeys(jedis);
            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
            }
        }
    }

    @Override
    public void del(String key) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(getWithPrefix(key));
        }
    }

    /**
     * Every entry of this region, keyed by logical key (no prefix), same as
     * {@link GeneralCacheMap#getAll()}.
     *
     * <p>
     * SCAN returns the full key, so the value has to be read with the raw key:
     * going through {@link #get(String)} would prefix it a second time and
     * yield null for every entry.
     */
    @Override
    public Map<String, String> getAll() {
        final Map<String, String> res = new LinkedHashMap<>();
        try (Jedis jedis = pool.getResource()) {
            final String fullPrefix = getWithPrefix("");
            for (String key : scanKeys(jedis)) {
                res.put(StringUtils.removeStart(key, fullPrefix), jedis.get(key));
            }
        }
        return res;
    }

    @Override
    public boolean setIfAbsent(String key, String value, long expirationMillis) {
        try (Jedis jedis = pool.getResource()) {
            SetParams params = SetParams.setParams().nx().px(expirationMillis);
            String result = jedis.set(getWithPrefix(key), value != null ? value : "", params);
            return "OK".equals(result);
        } catch (Exception ex) {
            JPMUtils.getLogger().error("error en redis setIfAbsent", ex);
            return false;
        }
    }
}
