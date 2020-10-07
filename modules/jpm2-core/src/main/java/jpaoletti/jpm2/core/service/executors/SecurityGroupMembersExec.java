package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.dao.DAO;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.security.Group;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author jpaoletti
 */
@Component
public class SecurityGroupMembersExec extends OperationExecutorSimple {

    @Autowired
    @Qualifier("jpm-dao-user")
    private DAO userDAO;

    @Override
    public Map<String, Object> prepare(List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(instances);
        for (EntityInstance instance : instances) {
            final Group group = (Group) instance.getIobject().getObject();
            prepare.put("members", userDAO.list(new DAOListConfiguration(Restrictions.eq("group.id", group.getId())).withAlias("groups", "group")));
        }
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
