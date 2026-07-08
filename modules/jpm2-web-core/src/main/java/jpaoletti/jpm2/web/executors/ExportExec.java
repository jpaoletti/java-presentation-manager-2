package jpaoletti.jpm2.web.executors;

import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Exportable;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.service.executors.OperationExecutorSimple;
import org.json.JSONArray;

/**
 * Generic export executor: serializes the selected {@link Exportable} instances to
 * a single JSON array and renders it in {@code op-export.jsp} for download.
 *
 * Works for both the item/SELECTED "export" and the "exportSelected" operations
 * (it simply iterates over the provided instances).
 *
 * @author jpaoletti
 */
public class ExportExec extends OperationExecutorSimple {

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        Map<String, Object> data = super.prepare(owner, ownerId, instances);
        if (instances == null || instances.isEmpty()) {
            throw new PMException("No hay registros para exportar");
        }

        JSONArray result = new JSONArray();
        for (EntityInstance instance : instances) {
            Object object = instance.getIobject().getObject();
            if (!(object instanceof Exportable exportable)) {
                throw new PMException("La entidad no implementa Exportable");
            }
            JSONArray exported = new JSONArray(exportable.export());
            for (int i = 0; i < exported.length(); i++) {
                result.put(exported.get(i));
            }
        }

        data.put("json", result.toString(2));
        data.put("operationTitle", "Export");
        data.put("fileName", getContext().getEntity().getId() + "-export.json");
        return data;
    }

    @Override
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        return null;
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }

    @Override
    public String getViewName(String operationId) {
        return "op-export";
    }

}
