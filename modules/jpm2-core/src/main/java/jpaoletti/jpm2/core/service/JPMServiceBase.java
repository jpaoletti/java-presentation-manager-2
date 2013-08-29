package jpaoletti.jpm2.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.converter.IgnoreConvertionException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.FieldValidator;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jpaoletti
 */
public class JPMServiceBase {

    @Autowired
    private JPMContext context;
    @Autowired
    private PresentationManager jpm;

    protected void processFields(Entity entity, Operation operation, Object object, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        preConversion(operation);
        for (Map.Entry<String, Object> entry : entityInstance.getValues().entrySet()) {
            final String[] param = parameters.get("field_" + entry.getKey());
            final Object newValue = param == null ? null : (param.length == 1 ? param[0] : param);
            final Field field = entity.getFieldById(entry.getKey());
            try {
                final Converter converter = field.getConverter(operation);
                final Object convertedValue = converter.build(field, object, newValue);
                final List<FieldValidator> validators = field.getValidators(operation);
                for (FieldValidator fieldValidator : validators) {
                    final Message msg = fieldValidator.validate(object, convertedValue);
                    if (msg != null) {
                        getContext().addFieldMsg(field, msg);
                    }
                }
                JPMUtils.set(object, field.getProperty(), convertedValue);
            } catch (IgnoreConvertionException e) {
            } catch (ConverterException e) {
                getContext().addFieldMsg(field, e.getMsg());
            }
        }
        if (!getContext().getFieldMessages().isEmpty()) {
            throw new ValidationException(null);
        }
    }

    public void preConversion(Operation operation) throws PMException {
        if (operation.getContext() != null) {
            operation.getContext().preConversion();
        }
    }

    public void preExecute(Operation operation, Object object) throws PMException {
        if (operation.getValidator() != null) {
            operation.getValidator().validate(object);
        }
        if (operation.getContext() != null) {
            operation.getContext().preExecute();
        }
    }

    public void postExecute(Operation operation) throws PMException {
        if (operation.getContext() != null) {
            operation.getContext().postExecute();
        }
    }

    protected void addFieldMsg(Map<String, List<Message>> messages, Field field, Message msg) {
        if (!messages.containsKey(field.getId())) {
            messages.put(field.getId(), new ArrayList<Message>());
        }
        messages.get(field.getId()).add(msg);
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }
}
