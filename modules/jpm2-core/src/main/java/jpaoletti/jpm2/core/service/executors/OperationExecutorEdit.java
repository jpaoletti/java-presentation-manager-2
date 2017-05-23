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
 *
 * @author jpaoletti
 */
@Component
public class OperationExecutorEdit implements OperationExecutor {

    @Autowired
    private PresentationManager jpm;

    @Override
    public Map<String, Object> prepare(List<EntityInstance> instances) throws PMException {
        return new LinkedHashMap<>();
    }

    @Override
    public void execute(JPMContext ctx, List<EntityInstance> instances, Map<String, String[]> parameters, Progress progress) throws PMException {
        for (EntityInstance instance : instances) {
            getJpm().getService().update(
                    ctx.getEntity(),
                    ctx.getEntityContext(),
                    ctx.getOperation(),
                    instance,
                    parameters
            );
        }
    }

    @Override
    public String getDefaultNextOperationId() {
        return "show";
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }
}
