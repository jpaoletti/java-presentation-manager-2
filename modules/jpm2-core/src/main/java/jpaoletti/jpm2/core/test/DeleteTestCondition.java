package jpaoletti.jpm2.core.test;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationCondition;

/**
 *
 * @author jpaoletti
 */
public class DeleteTestCondition implements OperationCondition {

    @Override
    public boolean check(EntityInstance instance, Operation operation, String displayAt) throws PMException {
        if (instance != null && instance.getIobject() != null && instance.getIobject().getObject() != null) {
            final JPMTest test = (JPMTest) instance.getIobject().getObject();
            return test.getBool();
        }
        return false;
    }

}
