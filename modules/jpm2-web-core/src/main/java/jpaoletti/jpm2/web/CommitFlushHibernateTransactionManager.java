package jpaoletti.jpm2.web;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;

public class CommitFlushHibernateTransactionManager extends HibernateTransactionManager {

    public CommitFlushHibernateTransactionManager(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        // Deja que Spring arranque la tx y haga lo suyo
        super.doBegin(transaction, definition);
        // Y enseguida forzamos el flush mode a COMMIT para esta sesión transaccional
        try {
            Session s = getSessionFactory().getCurrentSession();
            if (s != null && s.isOpen()) {
                s.setHibernateFlushMode(FlushMode.COMMIT);
            }
        } catch (Exception ignore) {
            // si no hay sesión bindeada aún, no hacemos nada (no debería pasar en tx)
        }
    }
}
