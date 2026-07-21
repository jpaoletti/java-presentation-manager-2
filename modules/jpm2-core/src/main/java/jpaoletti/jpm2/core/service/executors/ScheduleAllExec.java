package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.service.BatchService;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Reschedules all enabled batch jobs through {@link BatchService}.
 *
 * @author jpaoletti
 */
public class ScheduleAllExec extends OperationExecutorSimple {

    @Autowired(required = false)
    private BatchService batchService;

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        if (batchService != null) {
            batchService.scheduleAll();
        }
        getJpm().audit(ctx.getEntity(), ctx.getOperation(), null);
        return null;
    }

    @Override
    public String getDefaultNextOperationId() {
        return "list";
    }

    @Override
    public boolean immediateExecute() {
        return true;
    }

}
