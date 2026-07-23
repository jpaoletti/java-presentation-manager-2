package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.model.ThreadRunnerInstance;
import jpaoletti.jpm2.core.model.persistent.ThreadRunner;
import jpaoletti.jpm2.core.service.ThreadRunnerService;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Shows the detailed runtime status of a {@link ThreadRunner}.
 *
 * @author jpaoletti
 */
public class ShowThreadStatusExec extends OperationExecutorSimple {

    @Autowired(required = false)
    private ThreadRunnerService service;

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        Map<String, Object> data = super.prepare(owner, ownerId, instances);

        if (instances != null && !instances.isEmpty() && service != null) {
            EntityInstance instance = instances.get(0);
            ThreadRunner runner = (ThreadRunner) instance.getIobject().getObject();

            // Obtener información de estado del thread
            Map<String, Object> threadStatus = service.getThreadStatus(runner.getId());
            data.put("threadStatus", threadStatus);

            // Información adicional
            data.put("runnerId", runner.getId());
            data.put("runnerName", runner.getName());
            data.put("runnerDescription", runner.getDescription());
            data.put("runnerClass", runner.getClazz());
            data.put("runnerEnabled", runner.isEnabled());

            // Obtener parámetros configurados en BD
            Map<String, String> configuredParams = runner.getParameterMap();
            data.put("configuredParameters", configuredParams);

            // Si el thread existe, agregar sus parámetros en ejecución
            ThreadRunnerInstance threadInstance = service.getThreadInstance(runner.getId());
            if (threadInstance != null) {
                data.put("runtimeParameters", threadInstance.getParameters());
                data.put("threadName", threadInstance.getName());
                data.put("threadState", threadInstance.getState().name());
                data.put("threadAlive", threadInstance.isAlive());
                data.put("threadDaemon", threadInstance.isDaemon());
                data.put("threadInterrupted", threadInstance.isInterrupted());
                data.put("threadDoWork", threadInstance.isDoWork());
                data.put("threadDebug", threadInstance.isDebug());
            }

            // Información del servicio
            Map<Long, ThreadRunnerInstance> allThreads = service.getActiveThreads();
            data.put("totalActiveThreads", allThreads.size());
            data.put("allActiveThreadIds", allThreads.keySet());
        }

        return data;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        // No hace nada en la ejecución, solo muestra información
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return false; // Necesita mostrar la página de preparación
    }

}
