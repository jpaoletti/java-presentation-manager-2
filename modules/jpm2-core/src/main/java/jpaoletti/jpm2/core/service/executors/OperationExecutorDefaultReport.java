package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import org.springframework.stereotype.Component;

/**
 * @author jpaoletti
 */
@Component
public class OperationExecutorDefaultReport extends OperationExecutorSimple {

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        prepare.put("reportUrl", "/jpm/report/" + getContext().getEntity().getId() + "Report");
        return prepare;
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
