package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.log.DebugLog;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;

/**
 * Lightweight admin screen to control the in-memory {@link DebugLog} at runtime: set the global
 * level (0=off, 1=basic, 2=detailed, 3=trace), override a specific channel, or reset. Persistence
 * is intentionally NOT provided here (the level lives in memory and resets on restart) — apps that
 * need persistence declare a sysparam {@code debug} param, which pushes into DebugLog instead.
 *
 * <p>General scope, hosted on the {@code syslog} entity so any app that has the logging admin gets
 * it for free (auth {@code jpm.auth.operation.syslog.debugControl}). Not a {@code @Component}:
 * instantiated inline in the entity XML.
 *
 * @author jpaoletti
 */
public class DebugControlExec extends OperationExecutorSimple {

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        prepare.put("debugLevel", DebugLog.level());
        prepare.put("debugChannels", DebugLog.channels());
        prepare.put("debugMaxLevel", DebugLog.MAX_LEVEL);
        return prepare;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        final String action = getSimpleParameterValue(parameters, "action");
        if ("reset".equals(action)) {
            DebugLog.reset();
            return null;
        }
        final long ttl = parseLong(getSimpleParameterValue(parameters, "ttl"));
        final int level = parseInt(getSimpleParameterValue(parameters, "level"));
        final String channel = getSimpleParameterValue(parameters, "channel");
        if (channel != null && !channel.trim().isEmpty()) {
            DebugLog.setChannelLevel(channel.trim(), level, ttl);
        } else {
            DebugLog.setGlobalLevel(level, ttl);
        }
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }

    private static int parseInt(String s) {
        try {
            return s == null ? 0 : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static long parseLong(String s) {
        try {
            return s == null || s.trim().isEmpty() ? 0L : Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
