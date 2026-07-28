package jpaoletti.jpm2.core.log;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lightweight, runtime-controllable debug logging facility. Replaces the legacy
 * {@code ConfigService.debug(...)} mini-module without depending on any configuration store:
 * the on/off state is pure in-memory operational state (a debug flag is diagnostic, not
 * application config), so it lives in the logging layer and never touches the DB.
 *
 * <p>Instead of a single global boolean it uses a numeric <b>level</b> per <b>channel</b>:
 * <ul>
 *   <li>{@code 0} = OFF, {@code 1} = BASIC (milestones/decisions), {@code 2} = DETAILED
 *       (payloads, intermediate values), {@code 3} = TRACE (per-iteration, raw dumps).</li>
 *   <li>A global level applies to every channel; a per-channel override focuses one area
 *       (e.g. global {@code 0} but channel {@code prisma=3}) without flooding the rest.</li>
 *   <li>A call at level {@code L} on a channel logs only when the effective level of that
 *       channel is {@code >= L}. Everything defaults to {@code 0} (off).</li>
 * </ul>
 *
 * <p>Output flows through log4j2 loggers named {@code jpm.debug} (no channel) or
 * {@code jpm.debug.<channel>}, so it can be routed/filtered by normal appender config.
 * Levels can be enabled with an optional TTL so a channel left on in production turns
 * itself off again.
 *
 * <p>All methods are static and thread-safe.
 *
 * @author jpaoletti
 */
public final class DebugLog {

    /** Highest meaningful level (TRACE). Levels are clamped to {@code [0, MAX_LEVEL]}. */
    public static final int MAX_LEVEL = 3;

    private static final String ROOT = "jpm.debug";

    /** A configured threshold plus an optional expiry (epoch millis, {@code 0} = never). */
    private static final class Entry {

        volatile int level;
        volatile long expiry;

        Entry(int level, long expiry) {
            this.level = level;
            this.expiry = expiry;
        }
    }

    private static final Entry GLOBAL = new Entry(0, 0L);
    private static final Map<String, Entry> CHANNELS = new ConcurrentHashMap<>();

    private DebugLog() {
    }

    // ---- level resolution -------------------------------------------------

    /** Effective global level (expiring it first if its TTL elapsed). */
    public static int level() {
        return current(GLOBAL);
    }

    /** Effective level for a channel: its override if set, otherwise the global level. */
    public static int level(String channel) {
        if (channel == null || channel.isEmpty()) {
            return level();
        }
        final Entry e = CHANNELS.get(channel);
        if (e != null) {
            final int l = current(e);
            if (l > 0) {
                return l;
            }
            CHANNELS.remove(channel);
        }
        return level();
    }

    private static int current(Entry e) {
        if (e.expiry != 0L && System.currentTimeMillis() > e.expiry) {
            e.level = 0;
            e.expiry = 0L;
        }
        return e.level;
    }

    /** Whether the given channel would log at (or above) {@code level}. Useful to guard expensive blocks. */
    public static boolean enabled(String channel, int level) {
        return level >= 1 && level(channel) >= level;
    }

    // ---- logging ----------------------------------------------------------

    public static void debug(int level, Object message) {
        debug(null, level, message);
    }

    public static void debug(String channel, int level, Object message) {
        if (level >= 1 && level(channel) >= level) {
            logger(channel).info(message);
        }
    }

    public static void debug(int level, Supplier<?> message) {
        debug(null, level, message);
    }

    /** Lazy variant: the supplier runs only when the channel actually logs. */
    public static void debug(String channel, int level, Supplier<?> message) {
        if (level >= 1 && level(channel) >= level) {
            logger(channel).info(message == null ? null : message.get());
        }
    }

    private static Logger logger(String channel) {
        return LogManager.getLogger((channel == null || channel.isEmpty()) ? ROOT : ROOT + "." + channel);
    }

    // ---- runtime control (admin) -----------------------------------------

    public static void setGlobalLevel(int level) {
        setGlobalLevel(level, 0L);
    }

    public static void setGlobalLevel(int level, long ttlSeconds) {
        GLOBAL.level = clamp(level);
        GLOBAL.expiry = expiryOf(ttlSeconds);
    }

    public static void setChannelLevel(String channel, int level) {
        setChannelLevel(channel, level, 0L);
    }

    public static void setChannelLevel(String channel, int level, long ttlSeconds) {
        if (channel == null || channel.isEmpty()) {
            setGlobalLevel(level, ttlSeconds);
            return;
        }
        final int l = clamp(level);
        if (l <= 0) {
            CHANNELS.remove(channel);
        } else {
            CHANNELS.put(channel, new Entry(l, expiryOf(ttlSeconds)));
        }
    }

    /** Turns everything off (global level 0, no channel overrides). */
    public static void reset() {
        GLOBAL.level = 0;
        GLOBAL.expiry = 0L;
        CHANNELS.clear();
    }

    /** Snapshot of the active channel overrides (channel to effective level), for admin views. */
    public static Map<String, Integer> channels() {
        final Map<String, Integer> out = new TreeMap<>();
        for (final Map.Entry<String, Entry> e : CHANNELS.entrySet()) {
            final int l = current(e.getValue());
            if (l > 0) {
                out.put(e.getKey(), l);
            }
        }
        return out;
    }

    private static int clamp(int level) {
        if (level < 0) {
            return 0;
        }
        return level > MAX_LEVEL ? MAX_LEVEL : level;
    }

    private static long expiryOf(long ttlSeconds) {
        return ttlSeconds > 0 ? System.currentTimeMillis() + (ttlSeconds * 1000L) : 0L;
    }
}
