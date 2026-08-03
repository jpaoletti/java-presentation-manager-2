package jpaoletti.jpm2.core.entityparam;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.OperationContextSupport;

/**
 * {@code OperationContext} that, on {@code preExecute} (after the form fields are set on the new object and
 * before it is saved), auto-populates a {@link ParameterizedEntity}'s parameters from its effective catalog
 * (see {@link EntityParameterSeeder}). Wire it as the {@code context} of an entity's add operation so a new
 * parent comes pre-filled with the parameters — and defaults — of its selected implementation.
 *
 * <pre>
 * &lt;bean class="jpaoletti.jpm2.core.model.Operation"&gt;
 *   &lt;property name="id" value="add"/&gt;
 *   ...
 *   &lt;property name="context"&gt;&lt;bean class="...SeedEntityParametersOnAddContext"/&gt;&lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * @author jpaoletti
 */
public class SeedEntityParametersOnAddContext extends OperationContextSupport {

    @Override
    public void preExecute(Object object) throws PMException {
        if (object instanceof ParameterizedEntity) {
            EntityParameterSeeder.seed((ParameterizedEntity<?>) object);
        }
    }
}
