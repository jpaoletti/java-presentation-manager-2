package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.model.persistent.ThreadRunner;
import jpaoletti.jpm2.core.service.ThreadRunnerService;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * (Re)starts the selected {@link ThreadRunner} through {@link ThreadRunnerService}.
 *
 * @author jpaoletti
 */
public class ThreadRunnerExec extends OperationExecutorSimple {

    @Autowired(required = false)
    private ThreadRunnerService service;

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        for (EntityInstance instance : instances) {
            final ThreadRunner b = (ThreadRunner) instance.getIobject().getObject();
            if (service != null) {
                service.exec(b);
            }
            getJpm().audit(ctx.getEntity(), ctx.getOperation(), instance.getIobject(), b.getName());
        }
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return true;
    }
}
