package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.model.ThreadRunnerInstance;
import jpaoletti.jpm2.core.model.persistent.ThreadRunner;
import jpaoletti.jpm2.core.model.persistent.ThreadRunnerParameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Starts, stops and monitors {@link ThreadRunner} background threads.
 *
 * @author jpaoletti
 */
public class ThreadRunnerService extends JPMServiceBase implements DisposableBean {

    private volatile boolean shuttingDown = false;
    private volatile boolean started = false;

    private final Map<Long, ThreadRunnerInstance> threads = new LinkedHashMap<>();

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager txManager;

    @EventListener(ContextClosedEvent.class)
    public void onContextClosed(ContextClosedEvent event) {
        shuttingDown = true;
        stopAll();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed(ContextRefreshedEvent ev) {
        if (ev.getApplicationContext().getParent() != null) {
            return; // evitar doble firing
        }
        if (!started) {
            started = true;
            runAll();
        }
    }

    @Override
    public void destroy() {
        shuttingDown = true;
        stopAll();
    }

    public void runAll() {
        JPMUtils.getLogger().info("Iniciando Threads de tareas");
        try {
            threads.entrySet().stream().forEach((entry) -> {
                entry.getValue().clear();
            });
        } catch (Exception e) {
            JPMUtils.getLogger().info("Error iniciando Threads de tareas", e);
        }
        threads.clear();

        JPMUtils.sleep(3000l);
        try (StatelessSession s = getSessionFactory().openStatelessSession()) {
            try {
                final List<ThreadRunner> list = s.createQuery("SELECT DISTINCT r FROM ThreadRunner r WHERE r.enabled = true", ThreadRunner.class).list();
                list.stream().forEach(r -> {
                    r.setParameters(s.createQuery("FROM ThreadRunnerParameter WHERE threadRunner = :tr", ThreadRunnerParameter.class).setParameter("tr", r).list());
                    exec(r);
                });
            } catch (Exception ex) {
                JPMUtils.getLogger().error("Error al iniciar Threads", ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void exec(ThreadRunner r) {
        ThreadRunnerInstance thread = threads.get(r.getId());
        if (thread == null) {
            try {
                final Class<?> raw = Class.forName(r.getClazz());

                org.springframework.context.ApplicationContext ctx = null;
                try {
                    ctx = JPMUtils.getApplicationContext();
                } catch (Throwable ignore) {
                }

                if (ThreadRunnerInstance.class.isAssignableFrom(raw)) {
                    // Caso 1: clase extiende ThreadRunnerInstance
                    if (ctx != null) {
                        try {
                            // Si está registrada como @Component/@Bean, usar getBean (no fuerza setter injection)
                            thread = (ThreadRunnerInstance) ctx.getBean((Class<?>) raw);
                        } catch (org.springframework.beans.factory.NoSuchBeanDefinitionException nbd) {
                            // No está registrada: crear sin autowire por tipo ni dependency check
                            org.springframework.beans.factory.config.AutowireCapableBeanFactory abf = ctx.getAutowireCapableBeanFactory();
                            thread = (ThreadRunnerInstance) abf.createBean(
                                    (Class<? extends ThreadRunnerInstance>) raw,
                                    org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_NO,
                                    false // <- sin dependency check (evita 'contextClassLoader')
                            );
                            // Procesar solo anotaciones (@Autowired/@Value) si las hubiera
                            abf.autowireBean(thread);
                            abf.initializeBean(thread, null);
                        }
                    } else {
                        // Sin contexto Spring disponible: reflexión pura
                        thread = (ThreadRunnerInstance) raw.getDeclaredConstructor().newInstance();
                    }

                } else if (Runnable.class.isAssignableFrom(raw)) {
                    // Caso 2: clase implementa Runnable -> envolver en ThreadRunnerInstance
                    Runnable runnable;
                    if (ctx != null) {
                        try {
                            runnable = (Runnable) ctx.getBean((Class<?>) raw);
                        } catch (org.springframework.beans.factory.NoSuchBeanDefinitionException nbd) {
                            org.springframework.beans.factory.config.AutowireCapableBeanFactory abf = ctx.getAutowireCapableBeanFactory();
                            runnable = (Runnable) abf.createBean(
                                    (Class<? extends Runnable>) raw,
                                    org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_NO,
                                    false
                            );
                            abf.autowireBean(runnable);
                            abf.initializeBean(runnable, null);
                        }
                    } else {
                        runnable = (Runnable) raw.getDeclaredConstructor().newInstance();
                    }

                    final Runnable task = runnable;
                    thread = new ThreadRunnerInstance() {
                        @Override
                        public void run() {
                            task.run();
                        }
                    };
                    // Si el Runnable expone setParameters(Map), invocarlo best-effort
                    try {
                        java.lang.reflect.Method m = raw.getMethod("setParameters", java.util.Map.class);
                        m.invoke(task, r.getParameterMap());
                    } catch (NoSuchMethodException ignore) {
                        /* no soporta parámetros */ }
                } else {
                    throw new IllegalArgumentException("La clase " + r.getClazz() + " no es ThreadRunnerInstance ni Runnable");
                }

                // Configuración común
                thread.setName("ThreadRunnerInstance_" + r.getIdStr());
                thread.setDaemon(true); // no bloquea el shutdown del WAR
                thread.setContextClassLoader(Thread.currentThread().getContextClassLoader()); // evita fugas del classloader
                thread.setParameters(r.getParameterMap());
                thread.start();

                threads.put(r.getId(), thread);

            } catch (Exception ex) {
                JPMUtils.getLogger().error("Error al crear thread " + r.getName(), ex);
            }
        } else {
            thread.setParameters(r.getParameterMap());
        }
        JPMUtils.getLogger().info("Iniciado Thread " + r.getName());
    }

    public void stopAll() {
        threads.values().forEach(t -> {
            try {
                t.interrupt(); // tu ThreadRunnerInstance debería cooperar con interrupt()
            } catch (Exception ignore) {
            }
        });
        // esperar un poco a que terminen para no correr contra el cierre del DataSource
        threads.values().forEach(t -> {
            try {
                t.join(3000); // 3s por hilo; ajustá si necesitas
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        });
        threads.clear();
    }

    /**
     * Returns the running instance for a given ThreadRunner id, or null.
     */
    public ThreadRunnerInstance getThreadInstance(Long runnerId) {
        return threads.get(runnerId);
    }

    /**
     * Returns the full map of active threads.
     */
    public Map<Long, ThreadRunnerInstance> getActiveThreads() {
        return new LinkedHashMap<>(threads);
    }

    /**
     * Returns whether a specific ThreadRunner is currently running.
     */
    public boolean isThreadRunning(Long runnerId) {
        ThreadRunnerInstance instance = threads.get(runnerId);
        return instance != null && instance.isAlive();
    }

    /**
     * Returns detailed status information for a ThreadRunner.
     */
    public Map<String, Object> getThreadStatus(Long runnerId) {
        Map<String, Object> status = new LinkedHashMap<>();
        ThreadRunnerInstance instance = threads.get(runnerId);

        status.put("exists", instance != null);

        if (instance != null) {
            status.put("alive", instance.isAlive());
            status.put("state", instance.getState().name());
            status.put("daemon", instance.isDaemon());
            status.put("interrupted", instance.isInterrupted());
            status.put("name", instance.getName());
            status.put("doWork", instance.isDoWork());
            status.put("parameters", new LinkedHashMap<>(instance.getParameters()));
        }

        return status;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
