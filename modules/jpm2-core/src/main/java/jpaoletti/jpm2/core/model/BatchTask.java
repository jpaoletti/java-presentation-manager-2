package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.model.persistent.Batch;

/**
 * Task executed by a {@link Batch} job. Applications register their
 * implementations as Spring beans and reference the bean id in the batch
 * {@code task} field.
 *
 * @author jpaoletti
 */
public interface BatchTask {

    public void excecute(Batch batch);
}
