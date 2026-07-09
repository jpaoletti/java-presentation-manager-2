package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Duplicable;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Progress;

/**
 * Generic duplicate executor: creates a deep copy of each selected instance by
 * calling {@link Duplicable#duplicate()}, saving and auditing the new record.
 *
 * @author jpaoletti
 */
public class DuplicateExec extends OperationExecutorSimple {

    @Override
    @Transactional
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        for (EntityInstance instance : instances) {
            final Duplicable c = (Duplicable) instance.getIobject().getObject();
            final Duplicable duplicate = c.duplicate();
            getContext().getEntity().getDao().save(duplicate);
            final IdentifiedObject newIobject = new IdentifiedObject(duplicate.getId().toString(), duplicate);
            instance.setIobject(newIobject, ctx);
            getJpm().audit(ctx.getEntity(), ctx.getOperation(), newIobject, "from " + c.getId());
        }
        getContext().setGlobalMessage(MessageFactory.info("duplicate.success"));
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return true;
    }
}
