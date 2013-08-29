package jpaoletti.jpm2.core.test;

import java.util.Date;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.OperationContextSupport;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class JPMTestOperationContext extends OperationContextSupport {

    @Override
    public void preConversion() throws PMException {
        JPMUtils.getLogger().debug("[TEST] Pre-conversion");
    }

    @Override
    public void preExecute() throws PMException {
        JPMUtils.getLogger().debug("[TEST] Pre-execute");
        final JPMTest test = (JPMTest) getContext().getObject();
        test.setDate(new Date());
    }

    @Override
    public void postExecute() throws PMException {
        JPMUtils.getLogger().debug("[TEST] Post-execute");
    }
}
