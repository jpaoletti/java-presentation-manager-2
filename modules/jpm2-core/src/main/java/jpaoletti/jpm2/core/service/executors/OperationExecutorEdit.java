package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
public class OperationExecutorEdit extends OperationExecutorSimple {

    @Override
    public void execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
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

}
