package jpaoletti.jpm2.core.log;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
 *   <li>On top of that, a <b>scope</b> ({@link #pushScope(int, String)}) raises the effective
 *       level for the current thread only, during one unit of work. It lets a caller that knows
 *       something is worth tracing (a transaction of a product flagged for debugging, a single
 *       request) light up every existing debug call of that thread without turning debugging on
 *       for the whole system, and without touching a single call site. A scope only raises the
 *       level, it never lowers what the global/channel configuration already enables.</li>
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

    /** A thread-local level elevation, with an optional label prefixed to the messages it enables. */
    private static final class Scope {

        final int level;
        final String label;
        final Scope parent;

        Scope(int level, String label, Scope parent) {
            this.level = level;
            this.label = label;
            this.parent = parent;
        }
    }

    private static final ThreadLocal<Scope> SCOPE = new ThreadLocal<>();

    /**
     * Number of scopes open across all threads. It is the guard that keeps scopes free when nobody
     * is debugging: while it is zero no thread-local lookup happens at all, so the cost of the
     * feature on the normal path is a single volatile read.
     */
    private static final AtomicInteger ACTIVE_SCOPES = new AtomicInteger();

    private DebugLog() {
    }

    // ---- level resolution -------------------------------------------------

    /** Effective global level (expiring it first if its TTL elapsed), raised by the thread scope. */
    public static int level() {
        final int base = current(GLOBAL);
        final int scoped = scopeLevel();
        return scoped > base ? scoped : base;
    }

    /** Effective level for a channel: its override if set, otherwise the global level; raised by the thread scope. */
    public static int level(String channel) {
        if (channel == null || channel.isEmpty()) {
            return level();
        }
        final Entry e = CHANNELS.get(channel);
        if (e != null) {
            final int l = current(e);
            if (l > 0) {
                final int scoped = scopeLevel();
                return scoped > l ? scoped : l;
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

    // ---- thread scope -----------------------------------------------------

    /**
     * Raises the effective level for the current thread until the matching {@link #popScope()}.
     * Scopes nest, so every call <b>must</b> be paired in a {@code finally} block:
     *
     * <pre>
     * DebugLog.pushScope(level, "P:" + product.getId());
     * try {
     *     ...
     * } finally {
     *     DebugLog.popScope();
     * }
     * </pre>
     *
     * @param level level to guarantee within the scope, clamped to {@code [0, MAX_LEVEL]}
     * @param label optional tag prefixed to every message the scope enables, to tell the trace of
     * one unit of work apart from the rest of the log ({@code null} for none)
     */
    public static void pushScope(int level, String label) {
        SCOPE.set(new Scope(clamp(level), label, SCOPE.get()));
        ACTIVE_SCOPES.incrementAndGet();
    }

    /** Closes the innermost scope of the current thread, restoring the enclosing one. Null-safe. */
    public static void popScope() {
        final Scope scope = SCOPE.get();
        if (scope == null) {
            return;
        }
        if (scope.parent == null) {
            SCOPE.remove();
        } else {
            SCOPE.set(scope.parent);
        }
        //Never below zero: if a thread-local were wiped from the outside (container thread cleanup)
        //the counter would be the only thing left holding the fast path open.
        ACTIVE_SCOPES.updateAndGet(open -> open > 0 ? open - 1 : 0);
    }

    /**
     * Level of the innermost scope of the current thread, or {@code 0} when there is none. Callers
     * that hand work over to another thread capture it here and re-open the scope on the other side.
     */
    public static int scopeLevel() {
        final Scope scope = currentScope();
        return scope == null ? 0 : scope.level;
    }

    /** Label of the innermost scope of the current thread, or {@code null} when there is none. */
    public static String scopeLabel() {
        final Scope scope = currentScope();
        return scope == null ? null : scope.label;
    }

    private static Scope currentScope() {
        return ACTIVE_SCOPES.get() == 0 ? null : SCOPE.get();
    }

    /** Whether the given channel would log at (or above) {@code level}. Useful to guard expensive blocks. */
    public static boolean enabled(String channel, int level) {
        return level >= 1 && level(channel) >= level;
    }

    /** Whether debug is on (default channel, level 1); drop-in for the legacy {@code configService.isDebug()}. */
    public static boolean isDebug() {
        return level() >= 1;
    }

    /** Whether the default channel logs at (or above) {@code level}. */
    public static boolean isDebug(int level) {
        return level() >= level;
    }

    // ---- logging ----------------------------------------------------------

    /** Logs at BASIC level (1) on the default channel; drop-in for the legacy {@code configService.debug(x)}. */
    public static void debug(Object message) {
        debug(null, 1, message);
    }

    public static void debug(int level, Object message) {
        debug(null, level, message);
    }

    public static void debug(String channel, int level, Object message) {
        if (level >= 1 && level(channel) >= level) {
            emit(channel, message);
        }
    }

    public static void debug(int level, Supplier<?> message) {
        debug(null, level, message);
    }

    /** Lazy variant: the supplier runs only when the channel actually logs. */
    public static void debug(String channel, int level, Supplier<?> message) {
        if (level >= 1 && level(channel) >= level) {
            emit(channel, message == null ? null : message.get());
        }
    }

    /** Writes the message, prefixed with the scope label when one is open. */
    private static void emit(String channel, Object message) {
        final Scope scope = currentScope();
        if (scope != null && scope.label != null && !scope.label.isEmpty()) {
            logger(channel).info("[{}] {}", scope.label, message);
        } else {
            logger(channel).info(message);
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
