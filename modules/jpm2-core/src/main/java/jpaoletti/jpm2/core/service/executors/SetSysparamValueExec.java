package jpaoletti.jpm2.core.service.executors;

import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.model.persistent.Sysparam;
import jpaoletti.jpm2.core.service.SysparamService;
import jpaoletti.jpm2.core.sysparam.SysparamDef;
import jpaoletti.jpm2.core.sysparam.SysparamType;
import jpaoletti.jpm2.util.JPMUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Sets a sysparam value through {@link SysparamService#set} so validation, encryption
 * (for secrets), history and cache eviction all apply — unlike a raw entity edit.
 *
 * <p>Not a {@code @Component}: instantiated inline in the sysparam entity XML.
 *
 * @author jpaoletti
 */
public class SetSysparamValueExec extends OperationExecutorSimple {

    @Autowired
    private SysparamService service;

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        for (EntityInstance instance : instances) {
            final Sysparam param = (Sysparam) instance.getIobject().getObject();
            final String key = param.getKey();
            // Drive the editor from the catalog (source of truth for type and secrecy),
            // falling back to the persisted row for orphan/family keys. Combining both for
            // "secret" mirrors SysparamService.set so the form and the store always agree.
            final SysparamDef<?> def = service.getCatalog().defFor(key);
            final boolean secret = (def != null && def.isSecret()) || param.isSecret();
            final SysparamType type = def != null ? def.getType() : param.getType();
            prepare.put("id", param.getId());
            prepare.put("key", key);
            prepare.put("secret", secret);
            prepare.put("type", type != null ? type.name() : SysparamType.STRING.name());
            prepare.put("allowedValues", def != null ? def.getAllowedValues() : Collections.emptyList());
            prepare.put("required", def != null && def.isRequired());
            // Catalog default (formatted): drives the "restore default" button in the form.
            prepare.put("defaultValue", def != null ? def.getDefaultRaw() : null);
            // Do not prefill secrets; show the current plain value otherwise.
            prepare.put("currentValue", secret ? "" : service.getRaw(key));
        }
        return prepare;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        final String value = getSimpleParameterValue(parameters, "value");
        for (EntityInstance instance : instances) {
            final Sysparam param = (Sysparam) instance.getIobject().getObject();
            // Snapshot before the mutation so the standard detailed audit can diff the change
            // (the secret value is masked via Sysparam#maskInAudit). set() mutates this same
            // managed instance within the request session, so it reflects the new value after.
            final Map<String, Object> before = JPMUtils.getOriginalValues(ctx.getEntity(), param);
            service.set(param.getKey(), value, currentUser());
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
