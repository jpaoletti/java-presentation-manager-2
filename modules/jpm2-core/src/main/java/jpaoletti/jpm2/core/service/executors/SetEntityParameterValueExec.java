package jpaoletti.jpm2.core.service.executors;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.crypto.SysparamCipher;
import jpaoletti.jpm2.core.entityparam.EntityParameter;
import jpaoletti.jpm2.core.entityparam.EntityParameterCatalog;
import jpaoletti.jpm2.core.entityparam.EntityParameterDef;
import jpaoletti.jpm2.core.entityparam.EntityParameterResolver;
import jpaoletti.jpm2.core.entityparam.EntityParameters;
import jpaoletti.jpm2.core.entityparam.ParameterizedEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.sysparam.SysparamType;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Sets the value of an {@link EntityParameter} child through the catalog: validates against the parameter's
 * definition and <b>encrypts secret values</b> before persisting, so a secret never lands in the database in
 * plaintext. The catalog scope is provided as the {@code kind} bean property in the entity XML.
 *
 * <p>Not a {@code @Component}: instantiated inline in the child entity XML, e.g.
 * {@code <bean class="...SetEntityParameterValueExec"><property name="kind" value="gateway"/></bean>}.
 *
 * @author jpaoletti
 */
public class SetEntityParameterValueExec extends OperationExecutorSimple {

    private String kind;
    private String entityName;

    public void setKind(String kind) {
        this.kind = kind;
    }

    /** The JPM entity id of the parameter child (used to build the form action URL), e.g. "gatewayParameter". */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * The definition for a parameter child, resolved via the owner's instance-scoped catalog when the child
     * exposes its owner ({@link EntityParameter#getOwnerEntity()}), falling back to the global catalog by the
     * configured {@code kind}.
     */
    private EntityParameterDef<?> defFor(EntityParameter param) {
        final ParameterizedEntity<?> owner = param.getOwnerEntity();
        if (owner != null) {
            return EntityParameterResolver.defFor(owner, param.getName());
        }
        final EntityParameterCatalog catalog = EntityParameters.catalog();
        return catalog != null ? catalog.defFor(kind, param.getName()) : null;
    }

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        for (EntityInstance instance : instances) {
            final EntityParameter param = (EntityParameter) instance.getIobject().getObject();
            final EntityParameterDef<?> def = defFor(param);
            final boolean secret = def != null && def.isSecret();
            final SysparamType type = (def != null) ? def.getType()
                    : (param.getType() != null ? param.getType() : SysparamType.STRING);
            prepare.put("entityId", entityName != null ? entityName : (owner != null ? owner.getId() : null));
            prepare.put("id", instance.getIobject().getId());
            prepare.put("key", param.getName());
            prepare.put("secret", secret);
            prepare.put("type", type.name());
            prepare.put("allowedValues", def != null ? def.getAllowedValues() : Collections.emptyList());
            prepare.put("required", def != null && def.isRequired());
            prepare.put("defaultValue", def != null ? def.getDefaultRaw() : null);
            // Never prefill secrets; non-secrets are plaintext so the stored value is the current value.
            prepare.put("currentValue", secret ? "" : param.getValue());
        }
        return prepare;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        final String value = getSimpleParameterValue(parameters, "value");
        for (EntityInstance instance : instances) {
            final EntityParameter param = (EntityParameter) instance.getIobject().getObject();
            final EntityParameterDef<?> def = defFor(param);
            final boolean secret = def != null && def.isSecret();
            // Secret + blank input = keep the current secret (matches the form hint).
            if (secret && StringUtils.isBlank(value)) {
                continue;
            }
            if (def != null) {
                final String error = def.validate(value);
                if (error != null) {
                    throw new PMException(error);
                }
            }
            String stored = value;
            if (secret) {
                final SysparamCipher cipher = EntityParameters.cipher();
                if (cipher == null || !cipher.isEnabled()) {
                    throw new PMException("Cannot store a secret parameter: no enabled cipher is configured.");
                }
                stored = cipher.encrypt(value);
            }
            final Map<String, Object> before = JPMUtils.getOriginalValues(ctx.getEntity(), param);
            param.setValue(stored);
            ctx.getEntity().getDao().update(param);
            getJpm().audit(ctx.getEntity(), ctx.getOperation(), instance.getIobject(), before);
        }
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }

    static String currentUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}
