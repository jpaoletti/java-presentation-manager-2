package jpaoletti.jpm2.core.menu;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.service.executors.OperationExecutorSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adds to the dynamic menu a group with the entities that are not in it yet
 * ({@link JpmMenuService#generateFromEntities()}).
 *
 * <p>Not a {@code @Component}: instantiated inline in the jpmMenu entity XML.
 *
 * @author jpaoletti
 */
public class JpmMenuGenerateExec extends OperationExecutorSimple {

    @Autowired
    private JpmMenuService service;

    @Override
    @Transactional
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        final int created = service.generateFromEntities();
        getJpm().audit(ctx.getEntity(), ctx.getOperation(), null, String.valueOf(created));
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
