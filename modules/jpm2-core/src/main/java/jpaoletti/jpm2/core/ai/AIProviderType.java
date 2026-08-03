package jpaoletti.jpm2.core.ai;

import java.util.Arrays;
import java.util.List;
import jpaoletti.jpm2.core.entityparam.EntityParameterDef;

/**
 * Catalog of supported AI providers. Each value carries the stable {@code code} that matches the
 * {@link AIProviderImplementation#code()} of its implementation bean; {@code AIService} resolves the
 * implementation by that code (same enum-to-bean idea as the currency-converter catalog).
 *
 * <p>Each value also defines its <b>parameter catalog</b> (kind {@link #KIND}) — the entity-parameter defs of
 * an {@code AIConnector} of that provider, so the params tree/typing/secrecy follow the selected provider (the
 * same per-type pattern as {@code CacheType} for cache admins). Today the only real parameter is the
 * {@code api-key} (common to every provider); provider-specific knobs are added by overriding
 * {@link #parameterDefs()} on that constant.
 */
public enum AIProviderType {

    CLAUDE("claude") {
        @Override
        public List<EntityParameterDef<?>> parameterDefs() {
            return COMMON;
        }
    },
    OPENAI("openai") {
        @Override
        public List<EntityParameterDef<?>> parameterDefs() {
            return COMMON;
        }
    },
    GEMINI("gemini") {
        @Override
        public List<EntityParameterDef<?>> parameterDefs() {
            return COMMON;
        }
    };

    /** Entity-parameter catalog scope for AI connectors. */
    public static final String KIND = "ai-connector";

    /** Parameters common to every provider: the (encrypted) API key. */
    private static final List<EntityParameterDef<?>> COMMON = Arrays.<EntityParameterDef<?>>asList(
            EntityParameterDef.secret(KIND, "api-key").group("credentials").build()
    );

    private final String code;

    AIProviderType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    /** Parameter catalog (kind {@link #KIND}) of an {@code AIConnector} using this provider. */
    public abstract List<EntityParameterDef<?>> parameterDefs();
}
