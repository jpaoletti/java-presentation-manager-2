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
 * General batch-parameter catalog (kind {@code "batch"}) and the composition "general + the task's own". Here
 * live only the truly cross-task parameters (scheduling); every parameter specific to a task is declared by
 * that task's bean in its {@code params()} ({@link jpaoletti.jpm2.core.model.BatchTask} extends
 * {@link EntityParameterModule}), analogous to how a gateway composes the defs of its GI.
 *
 * <p>Each def is declared individually (verbose) so it is easy to read and re-classify. Types are deduced;
 * adjust as needed. Secrets are encrypted at rest and masked. Applications extend the catalog by declaring
 * parameters on their task beans (no change here needed).
 *
 * @author jpaoletti
 */
public final class BatchParamCatalog {

    public static final String KIND = "batch";

    private static final List<EntityParameterDef<?>> GENERAL = buildGeneral();
    private static final Map<String, List<EntityParameterDef<?>>> EFFECTIVE = new ConcurrentHashMap<>();

    private BatchParamCatalog() {
    }

    /** General catalog (common to every batch). */
    public static List<EntityParameterDef<?>> general() {
        return GENERAL;
    }

    /** Effective catalog of a batch: general + those declared by its task bean (cached by task bean id). */
    public static List<EntityParameterDef<?>> effective(String taskBeanId) {
        if (StringUtils.isBlank(taskBeanId)) {
            return GENERAL;
        }
        return EFFECTIVE.computeIfAbsent(taskBeanId, BatchParamCatalog::buildEffective);
    }

    private static List<EntityParameterDef<?>> buildEffective(String taskBeanId) {
        final List<EntityParameterDef<?>> result = new ArrayList<>(GENERAL);
        result.addAll(taskDefs(taskBeanId));
        return Collections.unmodifiableList(result);
    }

    /**
     * Reads the task bean's {@code params()} from the Spring context ({@code Batch.getTask()} is the bean id).
     * The task bean is a singleton, so this is a cheap context lookup; if the bean is missing or contributes
     * no params, it is ignored.
     */
    private static List<EntityParameterDef<?>> taskDefs(String taskBeanId) {
        try {
            final Object bean = JPMUtils.getApplicationContext().getBean(taskBeanId);
            if (bean instanceof EntityParameterModule) {
                final List<EntityParameterDef<?>> defs = ((EntityParameterModule) bean).params();
                return defs != null ? defs : Collections.<EntityParameterDef<?>>emptyList();
            }
        } catch (Throwable t) {
            JPMUtils.getLogger().warn("No se pudieron leer los parametros del task de batch '" + taskBeanId + "'", t);
        }
        return Collections.emptyList();
    }

    private static List<EntityParameterDef<?>> buildGeneral() {
        final List<EntityParameterDef<?>> d = new ArrayList<>();
        // Scheduling (leidos por el scheduler y por Batch.getHour()).
        d.add(EntityParameterDef.string(KIND, "hour").group("schedule").build());
        d.add(EntityParameterDef.string(KIND, "hora").group("schedule").build());
        return Collections.unmodifiableList(d);
    }
}
