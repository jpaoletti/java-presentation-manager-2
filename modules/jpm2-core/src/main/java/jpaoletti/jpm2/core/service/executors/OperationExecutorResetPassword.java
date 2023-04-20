package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.service.SecurityService;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author jpaoletti
 */
@Component
public class OperationExecutorResetPassword extends OperationExecutorSimple {

    @Autowired
    private SecurityService securityService;

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        for (EntityInstance instance : instances) {
            final UserDetails user = securityService.resetPassword(getContext().getEntity(), getContext().getEntityContext(), getContext().getOperation(), instance.getId());
            return "message:" + getJpm().getMessage("jpm.security.generatedpassword") + ": " + JPMUtils.get(user, "newPassword");
        }
        return null;
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
