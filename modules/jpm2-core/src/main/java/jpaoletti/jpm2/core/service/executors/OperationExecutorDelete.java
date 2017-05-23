package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.model.Progress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.OperationExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
public class OperationExecutorDelete implements OperationExecutor {

    @Autowired
    private PresentationManager jpm;

    @Override
    public Map<String, Object> prepare(List<EntityInstance> instances) throws PMException {
        return new LinkedHashMap<>();
    }

    @Override
    public void execute(JPMContext ctx, List<EntityInstance> instances, Map<String, String[]> parameters, Progress progress) throws PMException {
        progress.setCurrentProgress(1L);
        progress.setMaxProgress((long) instances.size());
        for (EntityInstance instance : instances) {
            getJpm().getService().delete(ctx.getEntity(), ctx.getEntityContext(), ctx.getOperation(), instance.getId());
            progress.inc();
        }
    }

    @Override
    public String getDefaultNextOperationId() {
        return "list";
    }

    @Override
    public boolean immediateExecute() {
        return true;
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }

}
