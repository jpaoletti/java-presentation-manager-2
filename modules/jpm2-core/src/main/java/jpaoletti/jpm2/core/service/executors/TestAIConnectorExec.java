package jpaoletti.jpm2.core.service.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.ai.AICompletion;
import jpaoletti.jpm2.core.ai.AIException;
import jpaoletti.jpm2.core.ai.AIRequest;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.model.persistent.AIConnector;
import jpaoletti.jpm2.core.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Sends a small test prompt through the selected {@link AIConnector} and records the outcome (model, tokens,
 * truncated response) in the audit trail; the full response is also visible in the {@code AICallLog} grid.
 * A one-click end-to-end smoke test of the AI module from the admin UI.
 *
 * <p>Not a {@code @Component}: instantiated inline in the aiConnector entity XML so only apps importing that
 * entity require an {@code AIService} bean. Inline beans still receive {@code @Autowired} injection.
 *
 * @author jpaoletti
 */
public class TestAIConnectorExec extends OperationExecutorSimple {

    private static final String DEFAULT_PROMPT = "Reply with the single word: OK";

    @Autowired
    private AIService service;

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        String prompt = getSimpleParameterValue(parameters, "prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            prompt = DEFAULT_PROMPT;
        }
        for (EntityInstance instance : instances) {
            final AIConnector connector = (AIConnector) instance.getIobject().getObject();
            try {
                final AICompletion completion = service.complete(connector, AIRequest.builder()
                        .maxTokens(64)
                        .user(prompt.trim())
                        .build());
                if (completion.isRefusal()) {
                    throw new PMException("The AI provider refused the request (finish reason: "
                            + completion.getFinishReason() + ")");
                }
                final String text = (completion.getText() == null) ? "" : completion.getText().trim();
                final int totalTokens = (completion.getUsage() != null) ? completion.getUsage().getTotalTokens() : 0;
                getJpm().audit(ctx.getEntity(), ctx.getOperation(), instance.getIobject(),
                        "model: " + completion.getModel() + " | tokens: " + totalTokens
                        + " | response: " + (text.length() > 300 ? text.substring(0, 300) : text));
            } catch (AIException e) {
                throw new PMException("AI call failed: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }
}
