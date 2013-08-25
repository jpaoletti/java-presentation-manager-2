package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;
import jpaoletti.jpm2.core.model.Field;
import org.hibernate.criterion.Criterion;

/**
 *
 * @author jpaoletti
 */
public class AddSearch extends EntityAction {

    private String fieldId;

    @Override
    public String execute() throws Exception {
        final String loadEntity = loadEntity();
        if (!loadEntity.equals(SUCCESS)) {
            return loadEntity;
        }
        if (getFieldId() == null || getFieldId().trim().equals("")) {
            getActionErrors().add("undefined.field.parameter");
            return ERROR;
        }
        final Field field = getEntity().getFieldById(getFieldId());
        if (field == null) {
            getActionErrors().add("undefined.field");
            return ERROR;
        }
        if (field.getSearcher() != null) {
            final Criterion build = field.getSearcher().build(field, getActionContext().getParameters());
            getSessionEntityData().getSearchCriteria().addDefinition(getFieldId(), build);
        }
        return SUCCESS;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }
}
