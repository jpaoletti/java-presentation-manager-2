package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.Map;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class EditAction extends OperationAction {

    public EditAction() {
        this.requireObject = true;
    }

    public String commit() throws PMException {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            //TO-DO. Validate information consistency
            for (Map.Entry<String, Object> entry : getInstance().getValues().entrySet()) {
                final String newValue = getStringParameter("field_" + entry.getKey());
                final Field field = getEntity().getFieldById(entry.getKey());
                preConversion();
                try {
                    final Converter converter = field.getConverter(getOperation());
                    JPMUtils.set(getObject(), field.getProperty(), converter.build(field, newValue));
                } catch (IgnoreConvertionException e) {
                }
            }
            preExecute();
            getEntity().getDao().update(getObject());
            postExecute();
            return FINISH;
        } else {
            return prepare;
        }
    }
}
