package jpaoletti.jpm2.core.service.executors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.Operation;
import org.springframework.stereotype.Component;

/**
 * Executor backing the generic "toPdf" operation. It does not commit anything;
 * it just prepares the model for {@code op-toPdf.jsp}, which lets the user pick
 * and reorder the columns and previews the generated PDF (served by
 * {@code /jpm/{entity}/toPdf}). Defaults (orientation/size/title) come from the
 * operation configuration.
 *
 * @author jpaoletti
 */
@Component
public class OperationExecutorToPdf extends OperationExecutorSimple {

    @Override
    public Map<String, Object> prepare(Entity owner, String ownerId, List<EntityInstance> instances) throws PMException {
        final Map<String, Object> prepare = super.prepare(owner, ownerId, instances);
        final Entity entity = getContext().getEntity();
        final Operation operation = getContext().getOperation();
        final String ectx = getContext().getEntityContext();

        final List<Map<String, String>> availableFields = new ArrayList<>();
        for (Field field : pdfFields(entity, ectx)) {
            final Map<String, String> f = new LinkedHashMap<>();
            f.put("id", field.getId());
            f.put("title", field.getTitle(entity));
            availableFields.add(f);
        }

        final String titleCfg = operation.getConfig("title");
        prepare.put("availableFields", availableFields);
        prepare.put("pdfBaseUrl", "jpm/" + entity.getId() + "/toPdf");
        prepare.put("orientation", operation.getConfig("orientation", "portrait"));
        prepare.put("size", operation.getConfig("size", "A4"));
        prepare.put("title", titleCfg != null ? operation.getMessage(titleCfg) : entity.getPluralTitle());
        return prepare;
    }

    /**
     * The candidate fields for the PDF: those with an explicit "toPdf" config;
     * when none is marked, fall back to the fields displayed for "toPdf".
     */
    private List<Field> pdfFields(Entity entity, String ectx) {
        final List<Field> marked = new ArrayList<>();
        final List<Field> displayed = new ArrayList<>();
        for (Field field : entity.getOrderedFields(ectx)) {
            if (field.hasConfigFor("toPdf")) {
                marked.add(field);
            }
            if (field.shouldDisplay("toPdf")) {
                displayed.add(field);
            }
        }
        return marked.isEmpty() ? displayed : marked;
    }

    @Override
    public String getDefaultNextOperationId() {
        return "list";
    }

    @Override
    public boolean immediateExecute() {
        return false;
    }
}
