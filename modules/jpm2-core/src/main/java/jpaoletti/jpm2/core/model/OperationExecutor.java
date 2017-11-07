package jpaoletti.jpm2.core.model;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;

/**
 * An operation executor is a bean able to execute an operation without the need
 * to implement a controller acton. Works only for instances operations at the
 * moment.
 *
 * @author jpaoletti
 */
public interface OperationExecutor {

    public Map<String, Object> prepare(List<EntityInstance> instances) throws PMException;

    /**
     * Preprocess de parameter map in case we need synchronic processing
     *
     * @param ctx
     * @param instances
     * @param parameters
     * @return
     * @throws PMException
     */
    public Map preExecute(JPMContext ctx, List<EntityInstance> instances, Map parameters) throws PMException;

    public void execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException;

    public String getDefaultNextOperationId();

    /**
     * if true, does not redirect to a preparation page after prepare, instead
     * executes right away.
     *
     * @return
     */
    public boolean immediateExecute();
}
