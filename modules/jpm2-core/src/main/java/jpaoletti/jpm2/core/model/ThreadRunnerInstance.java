package jpaoletti.jpm2.core.model;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.context.ApplicationContext;

/**
 * Base background task run by a {@link jpaoletti.jpm2.core.model.persistent.ThreadRunner}.
 * Applications extend this class and override {@link #work(Session)}; the runner
 * loop keeps the thread alive and opens a fresh Hibernate session per iteration.
 *
 * @author jpaoletti
 */
public class ThreadRunnerInstance extends Thread {

    public static final String PARAM_DO_WORK = "do-work";
    public static final String PARAM_OPEN_TX = "open-tx";

    private Map<String, String> parameters = new LinkedHashMap<>();

    @Override
    public void run() {
        JPMUtils.sleep(getParameter("start-delay", 2000l));
        if (!isContextActive()) {
            return;
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!isContextActive()) {
                    return;
                }
                // Si do-work es false, el thread sigue vivo pero en pausa
                if (!isDoWork()) {
                    JPMUtils.sleep(getParameter("not-working-delay", 2000l));
                    continue;
                }
                JPMUtils.executeInNewSessionNoTx(JPMUtils.getSessionFactory(), (Session session, org.hibernate.Transaction tx) -> {
                    try {
                        work(session);
                    } catch (Exception e) {
                        JPMUtils.getLogger().error("Error en Transation ThreadRunnerInstance: " + e.getMessage(), e);
                    }
                }, null);
            } catch (BeanCreationNotAllowedException | IllegalStateException b) {
                // Ocurre si Spring ya está cerrando el contexto
                return;
            } // BeanFactory not initialized or already closed
            catch (Exception e) {
                JPMUtils.getLogger().error("Error en ThreadRunnerInstance: " + e.getMessage(), e);
                JPMUtils.sleep(10000l);
                if (!isContextActive() || Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
            JPMUtils.sleep(getParameter("delay", 2000l));
        }
    }

    private boolean isContextActive() {
        try {
            ApplicationContext ctx = JPMUtils.getApplicationContext();
            if (ctx == null) {
                return false;
            }
            if (ctx instanceof org.springframework.context.support.AbstractApplicationContext) {
                return ((org.springframework.context.support.AbstractApplicationContext) ctx).isActive();
            }
            return true; // si no es de ese tipo, asumimos activo
        } catch (Throwable t) {
            return false;
        }
    }

    public void work(Session session) throws Exception {
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String name, String def) {
        final String value = getParameter(name);
        if (value == null) {
            return def;
        } else {
            return value;
        }
    }

    public String getParameter(String name) {
        if (getParameters() == null) {
            return null;
        }
        return getParameters().get(name);
    }

    public Integer getParameter(String name, Integer def) {
        try {
            return Integer.valueOf(getParameter(name, (def == null) ? null : def.toString()));
        } catch (Exception exception) {
            return def;
        }
    }

    public Long getParameter(String name, Long def) {
        return Long.valueOf(getParameter(name, (def == null) ? null : def.toString()));
    }

    public boolean getParameter(String name, boolean def) {
        return Boolean.parseBoolean(getParameter(name, Boolean.toString(def)));
    }

    public List<String> getParameter(String name, List<String> def) {
        final String value = getParameter(name);
        if (value == null || StringUtils.isEmpty(value)) {
            return def;
        } else {
            return Arrays.asList(value.split("[,]"));
        }
    }

    public boolean isDoWork() {
        return getParameter(PARAM_DO_WORK, false);
    }

    public boolean isDebug() {
        return getParameter("debug", false);
    }

    public void clear() {
        parameters.put(PARAM_DO_WORK, "false");
    }

}
