package jpaoletti.jpm2.web.executors;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Exportable;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.service.executors.OperationExecutorSimple;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Generic import executor: reads a JSON array (from an uploaded {@code .json} file
 * or a pasted textarea in {@code op-import.jsp}) and creates one entity per item by
 * calling {@link Exportable#importData(String)}, saving and auditing each one.
 *
 * @author jpaoletti
 */
public class ImportExec extends OperationExecutorSimple {

    public static final String HTTP_SERVLET_REQUEST = "HTTP_SERVLET_REQUEST";
    public static final String PARAM_JSON = "json";

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        Map<String, Object> data = super.prepare(owner, ownerId, instances);
        data.put("json", "");
        data.put("operationTitle", "Import");
        return data;
    }

    @Override
    @Transactional
    public String execute(JPMContext ctx, List<EntityInstance> instances, Map parameters, Progress progress) throws PMException {
        String json = resolveJson(parameters);
        if (StringUtils.isBlank(json)) {
            throw new PMException(MessageFactory.error("export.import.empty"));
        }

        JSONArray items;
        try {
            items = new JSONArray(json);
        } catch (JSONException exception) {
            throw new PMException(MessageFactory.error("export.import.invalidJson"), exception);
        }

        if (items.isEmpty()) {
            throw new PMException(MessageFactory.error("export.import.empty"));
        }

        int imported = 0;
        for (int i = 0; i < items.length(); i++) {
            Object value = items.get(i);
            if (!(value instanceof JSONObject object)) {
                throw new PMException(MessageFactory.error("export.import.invalidItem", Integer.toString(i + 1)));
            }
            Exportable exportable = createExportable();
            exportable.importData(object.toString());
            applyOwner(ctx, parameters, exportable);
            ctx.getEntity().getDao().save(exportable);
            final String newId = ctx.getEntity().getDao().getId(exportable).toString();
            getJpm().audit(ctx.getEntity(), ctx.getOperation(), new IdentifiedObject(newId, exportable));
            imported++;
        }

        ctx.setGlobalMessage(MessageFactory.success("export.import.success", Integer.toString(imported)));
        return null;
    }

    private String resolveJson(Map parameters) throws PMException {
        HttpServletRequest request = (HttpServletRequest) parameters.get(HTTP_SERVLET_REQUEST);
        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            MultipartFile file = multipartRequest.getFile("jsonFile");
            if (file != null && !file.isEmpty()) {
                try {
                    return new String(file.getBytes());
                } catch (IOException exception) {
                    throw new PMException("No se pudo leer el archivo JSON", exception);
                }
            }
        }
        return getSimpleParameterValue(parameters, PARAM_JSON);
    }

    private void applyOwner(JPMContext ctx, Map parameters, Exportable exportable) throws PMException {
        final Entity entity = ctx.getEntity();
        if (entity.isWeak()) {
            final Entity ownerEntity = (Entity) parameters.get(OWNER_ENTITY);
            final Object ownerIdRaw = parameters.get(OWNER_ID);
            final String ownerId = (ownerIdRaw instanceof String) ? (String) ownerIdRaw : getSimpleParameterValue(parameters, OWNER_ID);
            if (ownerEntity != null && ownerId != null) {
                final Object ownerObject = ownerEntity.getDao().get(ownerId);
                entity.getOwner().setOwnerObject(ctx.getEntityContext(), exportable, ownerObject);
            }
        }
    }

    private Exportable createExportable() throws PMException {
        try {
            Class<?> clazz = Class.forName(getContext().getEntity().getClazz());
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (!(instance instanceof Exportable exportable)) {
                throw new PMException("La entidad no implementa Exportable");
            }
            return exportable;
        } catch (PMException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new PMException("No se pudo crear la entidad para importar", exception);
        }
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }
}
