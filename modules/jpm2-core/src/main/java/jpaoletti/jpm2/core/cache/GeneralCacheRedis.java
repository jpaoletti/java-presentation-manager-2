package jpaoletti.jpm2.core.cache;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

/**
 * Redis-backed {@link GeneralCache} implementation using a Jedis pool. Keys are
 * namespaced with a global prefix plus the region prefix.
 *
 * @author jpaoletti
 */
public final class GeneralCacheRedis extends GeneralCache {

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
        if (StringUtils.isEmpty(user)) {
            // No user: AUTH with password only -> authenticates as the "default" user
            pool = new JedisPool(new JedisPoolConfig(), host, port, 5000, pass);
        } else {
            // Redis 6+ ACL: AUTH <user> <password>
            pool = new JedisPool(new JedisPoolConfig(), host, port, 5000, user, pass);
        }
        this.prefix = prefix;
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

    @Override
    public void clear() {
        Set<String> keys = pool.getResource().keys(getWithPrefix("*"));
        for (String key : keys) {
            pool.getResource().del(key);
        }
    }

    @Override
    public void del(String key) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(getWithPrefix(key));
        }
    }

    @Override
    public Map<String, String> getAll() {
        final Map<String, String> res = new java.util.LinkedHashMap<>();
        Set<String> keys = pool.getResource().keys(getWithPrefix("*"));
        for (String key : keys) {
            res.put(key, get(key));
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
