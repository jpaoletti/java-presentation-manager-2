package jpaoletti.jpm2.core.service;

import java.util.Date;
import jpaoletti.jpm2.core.dao.DefaultJPADAO;
import jpaoletti.jpm2.core.model.persistent.Syslog;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that records {@link Syslog} entries.
 *
 * <p>The former {@code syslog-activado}/{@code syslog-debug} DB config flags are
 * replaced by the {@code enabled}/{@code debug} bean properties (both default
 * {@code true}) so this service does not depend on the app ConfigService.
 *
 * @author jpaoletti
 */
public class SyslogService extends JPMServiceBase {

    @Autowired
    @Qualifier(value = "dao-syslog")
    private DefaultJPADAO syslogDAO;

    private boolean enabled = true;
    private boolean debug = true;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(Syslog.Severity severity, String permission, String message) {
        final Syslog log = new Syslog();
        try {
            final UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.setUsername(user.getUsername());
        } catch (Exception e) {
            log.setUsername("proceso");
        }
        log.setEventDate(new Date());
        log.setEventDatetime(new Date());
        log.setSeverity(severity);
        log.setPermission(permission);
        log.setMessage(message);
        if (enabled) {
            syslogDAO.save(log);
        }
        if (debug) {
            JPMUtils.getLogger().info(log.toString());
        }
    }

    public void log(Syslog.Severity severity, String message) {
        log(severity, null, message);
    }

    public void info(String message) {
        log(Syslog.Severity.Info, null, message);
    }

    public void info(String permission, String message) {
        log(Syslog.Severity.Info, permission, message);
    }

    public void advertencia(String message) {
        log(Syslog.Severity.Advertencia, null, message);
    }

    public void advertencia(String permission, String message) {
        log(Syslog.Severity.Advertencia, permission, message);
    }

    public void error(String message) {
        log(Syslog.Severity.Error, null, message);
    }

    public void error(String permission, String message) {
        log(Syslog.Severity.Error, permission, message);
    }

    public void critico(String message) {
        log(Syslog.Severity.Critico, null, message);
    }

    public void critico(String permission, String message) {
        log(Syslog.Severity.Critico, permission, message);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}
