package jpaoletti.jpm2.core.service.executors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.OperationExecutor;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Simple executor, do nothing.
 *
 * @author jpaoletti
 */
@Component
public class OperationExecutorSimple implements OperationExecutor {

    @Autowired
    private JPMContext context;

    @Autowired
    private PresentationManager jpm;

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final LinkedHashMap<String, Object> res = new LinkedHashMap<>();
        res.put(OWNER_ENTITY, owner);
        res.put(OWNER_ID, ownerId);
        return res;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        return null;
    }

    @Override
    public String getDefaultNextOperationId() {
        return "show";
    }

    @Override
    public boolean immediateExecute() {
        return false;
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

    public String getSimpleParameterValue(Map parameters, String name) {
        try {
            return ((String[]) parameters.get(name))[0];
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getViewName(String operationId) {
        return "op-" + operationId;
    }
}
