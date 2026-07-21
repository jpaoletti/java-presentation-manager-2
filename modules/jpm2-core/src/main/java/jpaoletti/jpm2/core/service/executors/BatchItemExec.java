package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.model.persistent.Batch;
import jpaoletti.jpm2.core.service.BatchService;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Runs the selected {@link Batch} immediately through {@link BatchService}.
 *
 * @author jpaoletti
 */
public class BatchItemExec extends OperationExecutorSimple {

    @Autowired(required = false)
    private BatchService batchService;

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        for (EntityInstance instance : instances) {
            final Batch b = (Batch) instance.getIobject().getObject();
            if (batchService != null) {
                batchService.exec(b);
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
