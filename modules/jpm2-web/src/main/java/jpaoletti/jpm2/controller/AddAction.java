package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.Map;
import static jpaoletti.jpm2.controller.OperationAction.FINISH;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.OperationScope;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class AddAction extends OperationAction {

    public AddAction() {
        this.requireObject = false;
    }

    @Override
    protected String prepare() throws PMException {
        final String prepare = super.prepare();
        if (SUCCESS.equals(prepare)) {
            setObject(JPMUtils.newInstance(getEntity().getClazz()));
            setInstance(new EntityInstance(getInstanceId(), getEntity(), getOperation(), getObject()));
            setItemOperations(getEntity().getOperationsFor(getObject(), getOperation(), OperationScope.ITEM));
        }
        return prepare;
    }

    public String commit() throws PMException {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            for (Map.Entry<String, Object> entry : getInstance().getValues().entrySet()) {
                final String newValue = getStringParameter("field_" + entry.getKey());
                final Field field = getEntity().getFieldById(entry.getKey());
                preConversion();
                try {
                    final Converter converter = field.getConverter(getOperation());
                    JPMUtils.set(getObject(), field.getProperty(), converter.build(field, newValue));
                } catch (IgnoreConvertionException e) {
                } catch (ConverterException e) {
                    getFieldMessages().put(field.getId(), e.getMsg());
                }
            }
            if (!getFieldMessages().isEmpty()) {
                return COMMIT_ERROR;
            }
            preExecute();
            getEntity().getDao().save(getObject());
            setInstanceId(getEntity().getDao().getId(getObject()).toString());
            postExecute();
            return FINISH;
        } else {
            return prepare;
        }
    }
}
