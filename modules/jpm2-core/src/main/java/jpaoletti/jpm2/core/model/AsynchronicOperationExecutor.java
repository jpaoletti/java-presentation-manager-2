package jpaoletti.jpm2.core.model;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.util.JPMUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 *
 * @author jpaoletti
 */
public class AsynchronicOperationExecutor extends Observable implements Runnable, Progresable, Observer {

    private final String id;
    private final OperationExecutor executor;
    private final List<EntityInstance> instances;
    private final Map parameters;
    private final SessionFactory sessionFactory;
    private final JPMContext ctx;
    private final Progress progress = new Progress();

    public AsynchronicOperationExecutor(String id, OperationExecutor executor, List<EntityInstance> instances, Map parameters, SessionFactory sessionFactory, JPMContext ctx) throws PMException {
        this.id = id;
        this.executor = executor;
        this.instances = instances;
        this.parameters = parameters;
        this.sessionFactory = sessionFactory;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        final Session session = getSessionFactory().openSession();
        TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));
        try {
            progress.addObserver(this);
            getExecutor().execute(ctx, instances, parameters, progress);
        } catch (Exception e) {
            JPMUtils.getLogger().error("AsynchronicOperationExecutor error", e);
        } finally {
            setChanged();
            notifyObservers(Boolean.TRUE);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(Boolean.FALSE);
    }

    public String getId() {
        return id;
    }

    public OperationExecutor getExecutor() {
        return executor;
    }

    public List<EntityInstance> getInstances() {
        return instances;
    }

    public Map getParameters() {
        return parameters;
    }

    @Override
    public Long getMaxProgress() {
        return progress.getMaxProgress();
    }

    @Override
    public Long getCurrentProgress() {
        return progress.getCurrentProgress();
    }

    @Override
    public Double getPercent() {
        return progress.getPercent();
    }

    @Override
    public String getStatus() {
        return progress.getStatus();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Progress getProgress() {
        return progress;
    }

    @Override
    public void inc() {
        getProgress().inc();
    }

    @Override
    public void setMaxProgress(Long maxProgress) {
        progress.setMaxProgress(maxProgress);
    }

    @Override
    public void setCurrentProgress(Long currentProgress) {
        progress.setCurrentProgress(currentProgress);
    }

    @Override
    public void setStatus(String status) {
        progress.setStatus(status);
    }

}
