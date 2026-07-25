package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.model.BatchTask;
import jpaoletti.jpm2.core.model.persistent.Batch;
import jpaoletti.jpm2.core.model.persistent.BatchParameter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Schedules and runs {@link Batch} jobs through {@link BatchTask} Spring beans.
 *
 * @author jpaoletti
 */
public class BatchService extends JPMServiceBase implements ApplicationContextAware {

    public static final int ONE_DAY = 1000 * 60 * 60 * 24;

    private final Map<Long, Timer> tasks = new LinkedHashMap<>();

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager txManager;

    @Value("${instance.type:PRIMARY}")
    private String instanceType;

    private ApplicationContext context;

    private volatile boolean contextRefreshed = false;

    public boolean isPrimaryInstance() {
        return instanceType == null || !instanceType.equalsIgnoreCase("SECONDARY");
    }

    /**
     * Schedules everything once the context is fully refreshed.
     *
     * Apps usually declare this service with {@code init-method="scheduleAll"},
     * which runs while the context is still being built (and even earlier if some
     * other bean created during the refresh autowires this service). Batch jobs
     * with no {@code hour} run 5 seconds after being scheduled, so on a slow
     * startup they could fire against a half built context. Waiting for the
     * refresh avoids that; see {@link ThreadRunnerService}, which does the same.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed(ContextRefreshedEvent ev) {
        if (ev.getApplicationContext().getParent() != null) {
            return; // the child (servlet) context event is propagated to the root: ignore it
        }
        contextRefreshed = true;
        scheduleAll();
    }

    public void scheduleAll() {
        if (!contextRefreshed) {
            JPMUtils.getLogger().info("Programacion de tareas diferida hasta que el contexto este listo");
            return;
        }
        if (isPrimaryInstance()) {
            JPMUtils.getLogger().info("Iniciando programacion de tareas");
            try {
                tasks.entrySet().stream().forEach((entry) -> {
                    if (entry.getValue() != null) {
                        entry.getValue().cancel();
                    }
                });
            } catch (Exception e) {
                JPMUtils.getLogger().info("Error iniciando programacion de tareas", e);
            }
            tasks.clear();

            try (StatelessSession s = getSessionFactory().openStatelessSession()) {
                try {
                    final List<Long> executed = new ArrayList<>();
                    final List<Batch> list = s.createQuery("SELECT DISTINCT b FROM Batch b WHERE b.enabled = true", Batch.class).list();
                    list.stream().forEach(batch -> {
                        if (!executed.contains(batch.getId())) {
                            batch.setParameters(s.createQuery("FROM BatchParameter WHERE batch = :batch", BatchParameter.class).setParameter("batch", batch).list());
                            final Timer timer = schedule(batch, batch.getHour());
                            tasks.put(batch.getId(), timer);
                            if (timer != null) {
                                JPMUtils.getLogger().info("Iniciada tarea " + batch.getDescription());
                            } else {
                                JPMUtils.getLogger().warn("Tarea NO iniciada: " + batch.getName() + " (" + batch.getDescription() + ")");
                            }
                            executed.add(batch.getId());
                        }
                    });
                } catch (Exception ex) {
                    JPMUtils.getLogger().error("Error al iniciar scheduler", ex);
                }
            }
        } else {
            JPMUtils.getLogger().info("Programacion de tareas ignorada. Instancia secundaria");
        }
    }

    protected Timer schedule(Batch batch, String time) {
        try {
            final Timer timer = new Timer();
            final InternalTimerTask task = new InternalTimerTask(
                    batch,
                    (BatchTask) getApplicationContext().getBean(batch.getTask().trim()));
            final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHH:mm:ss");
            if (StringUtils.isNotEmpty(time)) {
                Date et = sdf.parse(sdf.format(new Date()).substring(0, 6) + time);
                if (et.before(new Date())) {
                    et = DateUtils.addDays(et, 1);
                }
                timer.scheduleAtFixedRate(task, et, ONE_DAY);
            } else {
                timer.schedule(task, 5000L);
            }
            return timer;
        } catch (Exception ex) {
            JPMUtils.getLogger().error("Error al programar la tarea batch " + batch.getName(), ex);
            return null;
        }
    }

    public void exec(Batch batch) {
        try {
            final BatchTask task = (BatchTask) getApplicationContext().getBean(batch.getTask().trim());
            task.excecute(batch);
            JPMUtils.getLogger().info("Ejecutada tarea " + batch.getDescription());
        } catch (Exception ex) {
            JPMUtils.getLogger().error("Error al ejecutar tarea " + batch, ex);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }

    public static class InternalTimerTask extends TimerTask {

        private final Batch batch;
        private final BatchTask task;

        public InternalTimerTask(Batch batch, BatchTask task) {
            this.batch = batch;
            this.task = task;
        }

        @Override
        public void run() {
            try {
                task.excecute(batch);
            } catch (Exception e) {
                JPMUtils.getLogger().error("Error en la tarea programada: " + batch.getName(), e);
            }
        }
    }
}
