package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.entityparam.EntityParameterModule;
import jpaoletti.jpm2.core.model.persistent.Batch;

/**
 * Task executed by a {@link Batch} job. Applications register their
 * implementations as Spring beans and reference the bean id in the batch
 * {@code task} field.
 *
 * <p>Extends {@link EntityParameterModule}: a task declares in its {@code params()} the parameters PROPER to
 * that task (typed/secret), which {@link Batch} composes with the general batch params. Tasks with no own
 * parameters inherit the empty default.
 *
 * @author jpaoletti
 */
public interface BatchTask extends EntityParameterModule {

    public void excecute(Batch batch);
}
