package jpaoletti.jpm2.core.model.persistent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jpaoletti.jpm2.core.entityparam.EntityParameterDef;
import jpaoletti.jpm2.core.entityparam.EntityParameterModule;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * General thread-runner parameter catalog (kind {@code "thread-runner"}) and the composition
 * "general + the runner's own". Here live only the parameters read by the base
 * {@code ThreadRunnerInstance} loop; every parameter specific to a runner is declared by that runner's
 * {@code ThreadRunnerInstance} subclass in its {@code params()} ({@code ThreadRunnerInstance} implements
 * {@link EntityParameterModule}), analogous to how a gateway composes the defs of its GI.
 *
 * <p>Types deduced; adjust as needed. Applications extend the catalog by declaring parameters on their
 * subclasses (no change here needed).
 *
 * @author jpaoletti
 */
public final class ThreadRunnerParamCatalog {

    public static final String KIND = "thread-runner";

    private static final List<EntityParameterDef<?>> GENERAL = buildGeneral();
    private static final Map<String, List<EntityParameterDef<?>>> EFFECTIVE = new ConcurrentHashMap<>();

    private ThreadRunnerParamCatalog() {
    }

    /** General catalog (read by the base run loop, common to every runner). */
    public static List<EntityParameterDef<?>> general() {
        return GENERAL;
    }

    /** Effective catalog of a runner: general + those declared by its instance class (cached by class name). */
    public static List<EntityParameterDef<?>> effective(String className) {
        if (StringUtils.isBlank(className)) {
            return GENERAL;
        }
        return EFFECTIVE.computeIfAbsent(className, ThreadRunnerParamCatalog::buildEffective);
    }

    private static List<EntityParameterDef<?>> buildEffective(String className) {
        final List<EntityParameterDef<?>> result = new ArrayList<>(GENERAL);
        result.addAll(instanceDefs(className));
        return Collections.unmodifiableList(result);
    }

    /**
     * Reads {@code params()} from the runner's {@code ThreadRunnerInstance} subclass, instantiating it once by
     * reflection. Safe: the constructor is a plain {@code Thread} constructor (the loop only starts on
     * {@code start()}); if the class is missing or contributes no params it is ignored.
     */
    private static List<EntityParameterDef<?>> instanceDefs(String className) {
        try {
            final Class<?> clazz = Class.forName(className);
            if (EntityParameterModule.class.isAssignableFrom(clazz)) {
                final EntityParameterModule module = (EntityParameterModule) clazz.getDeclaredConstructor().newInstance();
                final List<EntityParameterDef<?>> defs = module.params();
                return defs != null ? defs : Collections.<EntityParameterDef<?>>emptyList();
            }
        } catch (Throwable t) {
            JPMUtils.getLogger().warn("No se pudieron leer los parametros del thread runner '" + className + "'", t);
        }
        return Collections.emptyList();
    }

    private static List<EntityParameterDef<?>> buildGeneral() {
        final List<EntityParameterDef<?>> d = new ArrayList<>();
        d.add(EntityParameterDef.bool(KIND, "do-work").group("base").defRaw("true").build());
        d.add(EntityParameterDef.bool(KIND, "debug").group("base").defRaw("false").build());
        d.add(EntityParameterDef.duration(KIND, "start-delay").group("base").defRaw("2000").build());
        d.add(EntityParameterDef.duration(KIND, "delay").group("base").defRaw("2000").build());
        d.add(EntityParameterDef.duration(KIND, "not-working-delay").group("base").defRaw("2000").build());
        return Collections.unmodifiableList(d);
    }
}
