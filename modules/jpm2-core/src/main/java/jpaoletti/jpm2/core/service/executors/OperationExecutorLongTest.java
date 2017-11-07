package jpaoletti.jpm2.core.service.executors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.OperationExecutor;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Test executor for progress and asynchronic operations.
 *
 * @author jpaoletti
 */
@Component
public class OperationExecutorLongTest implements OperationExecutor {

    @Autowired
    private JPMContext context;

    @Autowired
    private PresentationManager jpm;

    @Override
    public Map<String, Object> prepare(List<EntityInstance> instances) throws PMException {
        return new LinkedHashMap<>();
    }

    @Override
    public void execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        progress.setCurrentProgress(1L);
        progress.setMaxProgress(100L);
        for (int i = 1; i <= 100; i++) {
            System.out.println("jpaoletti.jpm2.core.service.executors.OperationExecutorLongTest.execute() " + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
            progress.setStatus("Processing " + i);
            progress.inc();
        }
    }

    @Override
    public String getDefaultNextOperationId() {
        return "show";
    }

    @Override
    public boolean immediateExecute() {
        return true;
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }

    @Override
    public Map preExecute(JPMContext ctx, List<EntityInstance> instances, Map parameters) throws PMException {
        return parameters;
    }

}
