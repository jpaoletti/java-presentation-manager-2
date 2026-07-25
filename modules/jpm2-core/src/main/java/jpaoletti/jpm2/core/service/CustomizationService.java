package jpaoletti.jpm2.core.service;

import java.io.InputStream;
import jpaoletti.jpm2.core.dao.JPADAO;
import jpaoletti.jpm2.core.model.persistent.Customization;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StreamUtils;

/**
 * Access to the system customization ({@link Customization}).
 *
 * To enable it in an app add to spring-jpm: &lt;bean id="customizationService" class="jpaoletti.jpm2.core.service.CustomizationService" /&gt;
 *
 * The id of the customization row is taken from the {@code customizationId}
 * property (default 1). It replaces the old configService.getLong("customization-id", 1L),
 * since JPM2 does not provide a generic ConfigService.
 *
 * It deliberately does NOT extend {@link JPMServiceBase}: this service is a
 * dependency of {@link jpaoletti.jpm2.core.i18n.DbMessageSource}, which Spring
 * instantiates inside {@code initMessageSource()}, very early in the context
 * refresh. Inheriting the {@code @Autowired PresentationManager} of the base
 * class made that early instantiation drag in the whole JPM bean graph (the
 * PresentationManager autowires {@code Map<String, Entity>}, so every entity and
 * every executor they reference got created too, e.g. ScheduleAllExec ->
 * batchService, whose init-method scheduled all the batch jobs while the context
 * was still refreshing).
 *
 * @author jpaoletti
 */
public class CustomizationService {

    @Autowired
    @Qualifier(value = "dao-customization")
    private JPADAO customizationDAO;

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    private long customizationId = 1L;

    private byte[] defaultLogo;

    /**
     * Retrieves the persisted customization row, or {@code null} when it does not
     * exist or cannot be read.
     *
     * It is intentionally NOT {@code @Transactional}: it uses the ambient session
     * (OpenSessionInView / active transaction) when there is one, and opens a
     * short-lived session of its own when there is none. The latter is the case
     * when it is called during context refresh (e.g. from
     * {@link jpaoletti.jpm2.core.i18n.DbMessageSource}, which Spring initializes
     * long before any request or transaction exists): asking for
     * {@code getCurrentSession()} there fails with
     * "Could not obtain transaction-synchronized Session for current thread".
     *
     * @return the persisted customization or null
     */
    public Customization findCustomization() {
        try {
            if (sessionFactory == null
                    || TransactionSynchronizationManager.hasResource(sessionFactory)
                    || TransactionSynchronizationManager.isSynchronizationActive()) {
                return (Customization) customizationDAO.get(String.valueOf(customizationId));
            }
            return findInOwnSession();
        } catch (Exception e) {
            JPMUtils.getLogger().debug("Customization could not be read", e);
            return null;
        }
    }

    /**
     * Reads the customization binding a brand new session to the thread, so any
     * DAO down the line can still rely on {@code getCurrentSession()}.
     */
    private Customization findInOwnSession() {
        final Session session = sessionFactory.openSession();
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        try {
            return (Customization) customizationDAO.get(String.valueOf(customizationId));
        } finally {
            TransactionSynchronizationManager.unbindResourceIfPossible(sessionFactory);
            session.close();
        }
    }

    /**
     * Retrieves the customization, never null: when there is no persisted row (or
     * it cannot be read) a transient one carrying the default logos is returned.
     */
    public Customization getCustomization() {
        Customization c = findCustomization();
        if (defaultLogo == null) {
            try (InputStream is = getClass().getResourceAsStream("/defaultLogo.png")) {
                if (is != null) {
                    defaultLogo = StreamUtils.copyToByteArray(is);
                }
            } catch (Exception ex) {
                JPMUtils.getLogger().error("Error loading /defaultLogo.png", ex);
            }
        }
        if (c == null) {
            c = new Customization();
            c.setLogo(defaultLogo);
            c.setLoginLogo(defaultLogo);
        } else {
            if (c.getLoginLogo() == null) {
                c.setLoginLogo(defaultLogo);
            }
            if (c.getLogo() == null) {
                c.setLogo(defaultLogo);
            }
        }
        return c;
    }

    public long getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(long customizationId) {
        this.customizationId = customizationId;
    }

    @SuppressWarnings("unchecked")
    public JPADAO<Customization, Long> getCustomizationDAO() {
        return customizationDAO;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
