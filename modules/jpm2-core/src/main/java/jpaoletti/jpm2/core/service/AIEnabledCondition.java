package jpaoletti.jpm2.core.service;

import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationCondition;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link OperationCondition} that shows/enables an operation only when AI is enabled for a given
 * {@code purpose} — i.e. an active connector exists for it and every {@link jpaoletti.jpm2.core.ai.AIEntitlementResolver}
 * allows it. Declare one bean per purpose and reference it as the operation's condition:
 *
 * <pre>{@code
 * <bean id="aiEnabled-myPurpose" class="jpaoletti.jpm2.core.service.AIEnabledCondition">
 *     <property name="purpose" value="my-purpose" />
 * </bean>
 * }</pre>
 */
public class AIEnabledCondition implements OperationCondition {

    @Autowired
    private AIService aiService;

    private String purpose;

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public boolean check(EntityInstance instance, Operation operation, String displayAt) throws PMException {
        return aiService.isEnabled(purpose);
    }
}
