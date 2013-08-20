package jpaoletti.jpm2.core;

import java.util.List;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Operation;

/**
 *
 * @author jpaoletti
 */
public interface OperationController {

    public Entity getEntity();

    public String getEntityId();

    public Object getObject();

    public List<Operation> getItemOperations();

    public List<Operation> getGeneralOperations();

    public List<Operation> getSelectedOperations();
}
