package jpaoletti.jpm2.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.message.Message;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityInstance;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.FieldValidator;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.core.model.ValidationException;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 *
 * @author jpaoletti
 */
public class JPMServiceBase {

    @Autowired
    private JPMContext context;

    @Autowired
    private PresentationManager jpm;

    @Autowired
    private AuthorizationService authorizationService;

    public void processFields(Entity entity, Operation operation, Object object, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        preConversion(operation, object);
        for (Map.Entry<String, Object> entry : entityInstance.getValues().entrySet()) {
            final String[] param = parameters.get("field_" + entry.getKey());
            final Object newValue = param == null ? null : (param.length == 1 ? param[0] : param);
            final Field field = entity.getFieldById(entry.getKey(), getContext().getEntityContext());
            try {
                final Converter converter = field.getConverter(entityInstance, operation);
                boolean set = true;
                final Object convertedValue = converter != null ? converter.build(getContext().getContextualEntity(), field, object, newValue) : null;
                if (converter != null) {
                    final List<FieldValidator> validators = field.getValidators(entityInstance, operation);
                    for (FieldValidator fieldValidator : validators) {
                        final Message msg = fieldValidator.validate(object, convertedValue);
                        if (msg != null) {
                            getContext().addFieldMsg(field, msg);
                            set = false;
                        }
                    }
                } else {
                    set = false;
                }
                if (set) {
                    JPMUtils.set(object, field.getProperty(), convertedValue);
                }
            } catch (IgnoreConvertionException e) {
            } catch (ConverterException e) {
                getContext().addFieldMsg(field, e.getMsg());
            }
        }
        if (!getContext().getFieldMessages().isEmpty()) {
            throw new ValidationException(object);
        }
    }

    public void preConversion(Operation operation, Object object) throws PMException {
        if (operation.getContext() != null) {
            operation.getContext().preConversion(object);
        }
    }

    public void preExecute(Operation operation, Object object) throws PMException {
        if (operation.getValidator() != null && object != null) {
            try {
                operation.getValidator().validate(object);
            } catch (ValidationException ve) {
                getContext().getEntity().getDao().detach(object); //Avoid flush of changes
                ve.setValidatedObject(object);
                throw ve;
            }
        }
        if (operation.getContext() != null) {
            operation.getContext().preExecute(object);
        }
    }

    public void postExecute(Operation operation, Object object) throws PMException {
        if (operation.getContext() != null) {
            operation.getContext().postExecute(object);
        }
    }

    protected void addFieldMsg(Map<String, List<Message>> messages, Field field, Message msg) {
        if (!messages.containsKey(field.getId())) {
            messages.put(field.getId(), new ArrayList<>());
        }
        messages.get(field.getId()).add(msg);
    }

    public String getMessage(String key, Object... params) {
        return getJpm().getMessage(key, params);
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

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
