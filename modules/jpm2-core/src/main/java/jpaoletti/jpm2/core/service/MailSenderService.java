package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.mail.GeneralMailSender;
import jpaoletti.jpm2.core.mail.Mail;
import jpaoletti.jpm2.core.model.persistent.MailSender;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DefaultJPADAO;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Service that keeps a live cache of configured mail senders and dispatches
 * emails asynchronously through them.
 *
 * @author jpaoletti
 */
public class MailSenderService extends JPMServiceBase {

    @Autowired
    @Qualifier(value = "dao-mailSender")
    private DefaultJPADAO mailSenderDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Map<String, GeneralMailSender> senders;

    public void init() {
        JPMUtils.getLogger().info("Iniciando servicio de envio de mails");
        senders = new LinkedHashMap<>();
        final Session session = getSessionFactory().openSession();
        TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));
        try {
            final List list = mailSenderDAO.list(null);
            list.stream().forEach(o -> {
                reload((MailSender) o);
            });
        } catch (Exception e) {
            JPMUtils.getLogger().warn("No se pudieron cargar los enviadores de mails. "
                    + "El servicio queda sin enviadores hasta ejecutar la migracion y recargar.", e);
        } finally {
            TransactionSynchronizationManager.unbindResourceIfPossible(getSessionFactory());// Without this the second invocation fails?
            session.close();
        }
    }

    /**
     * Sends a mail asynchronously.
     *
     * @param code If code is empty, nothing is sent. If it does not exist a
     * warning is logged.
     *
     * @param mail
     */
    public void send(String code, Mail mail) {
        if (!StringUtils.isEmpty(code)) {
            if (!senders.containsKey(code)) {
                JPMUtils.getLogger().warn(String.format("No existe el enviador de mails '%s'", code));
            } else {
                senders.get(code).send(mail);
            }
        }
    }

    /**
     * Sends a mail synchronously through the sender identified by {@code code},
     * propagating any failure. Throws if the code is not configured, so
     * interactive callers get a real error instead of a silent no-op.
     */
    public void sendSync(String code, Mail mail) throws Exception {
        final GeneralMailSender sender = (senders == null) ? null : senders.get(code);
        if (sender == null) {
            throw new PMException(String.format("No existe el enviador de mails '%s'", code));
        }
        sender.sendSync(mail);
    }

    public GeneralMailSender getCache(String code) {
        return senders.get(code);
    }

    public void reload(MailSender mailSender) {
        senders.put(mailSender.getName(), mailSender.getSenderType().build(mailSender.getParameterMap()));
        JPMUtils.getLogger().info("Iniciando enviador de mail '" + mailSender.getDescription() + "'");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
