package jpaoletti.jpm2.web.executors;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import javax.servlet.http.HttpServletRequest;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Exportable;
import jpaoletti.jpm2.core.model.IdentifiedObject;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.OperationValidator;
import jpaoletti.jpm2.core.model.Progress;
import jpaoletti.jpm2.core.model.ValidationException;
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
 * <p>
 * When the import operation declares a {@code validator}, every imported item is
 * checked with it before being saved, so an item coming from a file is subject to
 * the very same consistency rules as one typed in the add form. A rejected item
 * aborts the whole import (the executor is transactional).
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
            validate(ctx, exportable, i + 1);
            ctx.getEntity().getDao().save(exportable);
            final String newId = ctx.getEntity().getDao().getId(exportable).toString();
            getJpm().audit(ctx.getEntity(), ctx.getOperation(), new IdentifiedObject(newId, exportable));
            imported++;
        }

        ctx.setGlobalMessage(MessageFactory.success("export.import.success", Integer.toString(imported)));
        return null;
    }

    /**
     * Applies the operation validator, if any, to an item about to be imported.
     *
     * @param ctx current context
     * @param exportable the item already populated from the JSON
     * @param itemNumber 1 based position of the item inside the array, for the error message
     * @throws PMException when the validator rejects the item
     */
    private void validate(JPMContext ctx, Exportable exportable, int itemNumber) throws PMException {
        final Operation operation = ctx.getOperation();
        final OperationValidator validator = (operation != null) ? operation.getValidator() : null;
        if (validator == null) {
            return;
        }
        try {
            validator.validate(exportable);
        } catch (ValidationException exception) {
            final Message msg = exception.getMsg();
            if (msg == null) {
                throw new PMException(MessageFactory.error("export.import.itemRejected",
                        Integer.toString(itemNumber)), exception);
            }
            throw new PMException(MessageFactory.error("export.import.invalidItemValidation",
                    Integer.toString(itemNumber), msg.getText()), exception);
        }
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
