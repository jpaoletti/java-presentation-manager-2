package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.cache.GeneralCache;
import jpaoletti.jpm2.core.cache.GeneralCacheMap;
import jpaoletti.jpm2.core.model.persistent.CacheAdmin;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.dao.DefaultJPADAO;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Service that keeps a live registry of cache regions. Each {@link CacheAdmin} row
 * defines a named region (its {@code code}) backed by an in-memory map or Redis.
 * Unknown region codes are served with a throwaway in-memory map, so callers never
 * get a null cache.
 *
 * @author jpaoletti
 */
public class CacheService extends JPMServiceBase {

    @Autowired
    @Qualifier(value = "dao-cacheAdmin")
    private DefaultJPADAO cacheAdminDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private Map<String, GeneralCache> caches = new LinkedHashMap<>();

    public void init() {
        JPMUtils.getLogger().info("Iniciando servicio de caches");
        caches = new LinkedHashMap<>();
        final Session session = getSessionFactory().openSession();
        TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));
        try {
            final List list = cacheAdminDAO.list(null);
            list.stream().forEach(o -> reload((CacheAdmin) o));
        } catch (Exception e) {
            JPMUtils.getLogger().warn("No se pudieron cargar las caches. "
                    + "El servicio queda sin caches configuradas hasta ejecutar la migracion y recargar.", e);
        } finally {
            TransactionSynchronizationManager.unbindResourceIfPossible(getSessionFactory());// Without this the second invocation fails?
            session.close();
        }
    }

    public GeneralCache getCache(String code) {
        return caches.computeIfAbsent(code, k -> new GeneralCacheMap(new LinkedHashMap<>()));
    }

    public GeneralCache getCacheOrNull(String code) {
        return caches.get(code);
    }

    public void reload(CacheAdmin cacheAdmin) {
        caches.put(cacheAdmin.getCode(), cacheAdmin.getCacheType().build(cacheAdmin.getParameterMap(), cacheAdmin.getCode()));
        JPMUtils.getLogger().info("Iniciando cache '" + cacheAdmin.getDescription() + "'");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
