package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.stereotype.Component;

/**
 * Test executor for progress and asynchronic operations.
 *
 * @author jpaoletti
 */
@Component
public class OperationExecutorLongTest extends OperationExecutorSimple {

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

}
