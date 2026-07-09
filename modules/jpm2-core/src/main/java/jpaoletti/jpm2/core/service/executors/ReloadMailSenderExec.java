package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.model.persistent.MailSender;
import jpaoletti.jpm2.core.service.MailSenderService;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Reloads the runtime cache of the selected {@link MailSender} instances.
 *
 * <p>Not a {@code @Component}: it is instantiated inline in the mailSender entity
 * XML so that only apps importing that entity require a {@code MailSenderService}
 * bean. Inline beans still receive {@code @Autowired} injection via the app's
 * component scan.
 *
 * @author jpaoletti
 */
public class ReloadMailSenderExec extends OperationExecutorSimple {

    @Autowired
    private MailSenderService service;

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        for (EntityInstance instance : instances) {
            final MailSender c = (MailSender) instance.getIobject().getObject();
            service.reload(c);
            getJpm().audit(ctx.getEntity(), ctx.getOperation(), instance.getIobject(), c.getName());
        }
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return true;
    }
}
