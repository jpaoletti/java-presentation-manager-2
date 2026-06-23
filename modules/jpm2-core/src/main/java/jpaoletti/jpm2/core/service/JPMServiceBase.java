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

/**
 *
 * @author jpaoletti
 */
public class JPMServiceBase {

    private static final org.apache.logging.log4j.Logger LOG = JPMUtils.getLogger(JPMUtils.SERVICE);

    @Autowired
    private JPMContext context;

    @Autowired
    private PresentationManager jpm;

    @Autowired
    private AuthorizationService authorizationService;

    public void processFields(Entity entity, Operation operation, Object object, EntityInstance entityInstance, Map<String, String[]> parameters) throws PMException {
        LOG.debug("processFields IN entity={} op={} fields={}", entity.getId(), operation, entityInstance.getValues().size());
        preConversion(operation, object);
        for (Map.Entry<String, Object> entry : entityInstance.getValues().entrySet()) {
            final String[] param = parameters.get("field_" + entry.getKey());
            final Object newValue = param == null ? null : (param.length == 1 ? param[0] : param);
            final Field field = entity.getFieldById(entry.getKey(), getContext().getEntityContext());
            try {
                final Converter converter = field.getConverter(entityInstance, operation);
                boolean set = true;
                final Object convertedValue;
                try {
                    convertedValue = converter != null ? converter.build(getContext().getContextualEntity(), field, object, newValue) : null;
                } catch (IgnoreConvertionException ice) {
                    // Do not log: will happen for any readonly property
                    throw ice;
                } catch (Exception e) {
                    LOG.error("processFields conversion error entity={} op={} field={} property={} rawValue={}",
                            entity.getId(), operation, field.getId(), field.getProperty(), newValue, e);
                    throw e;
                }
                if (converter != null) {
                    final List<FieldValidator> validators = field.getValidators(entityInstance, operation);
                    for (FieldValidator fieldValidator : validators) {
                        final Message msg = fieldValidator.validate(object, convertedValue);
                        if (msg != null) {
                            LOG.debug("processFields field={} validation FAILED msg={}", field.getId(), msg.getKey());
                            getContext().addFieldMsg(field, msg);
                            set = false;
                        }
                    }
                } else {
                    set = false;
                }
                if (set) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processFields field={} property={} value={}", field.getId(), field.getProperty(),
                                JPMUtils.maskValue(field.getId(), String.valueOf(convertedValue)));
                    }
                    JPMUtils.set(object, field.getProperty(), convertedValue);
                }
            } catch (IgnoreConvertionException e) {
                LOG.debug("processFields field={} ignored conversion", field.getId());
            } catch (ConverterException e) {
                LOG.debug("processFields field={} converter error msg={}", field.getId(), e.getMsg() != null ? e.getMsg().getKey() : null);
                getContext().addFieldMsg(field, e.getMsg());
            }
        }
        if (!getContext().getFieldMessages().isEmpty()) {
            LOG.debug("processFields OUT entity={} VALIDATION FAILED fieldMsgs={}", entity.getId(), getContext().getFieldMessages().keySet());
            throw new ValidationException(object);
        }
        LOG.debug("processFields OUT entity={} ok", entity.getId());
    }

    public void preConversion(Operation operation, Object object) throws PMException {
        if (operation.getContext() != null) {
            operation.getContext().preConversion(object);
        }
    }

    public void preExecute(Operation operation, Object object) throws PMException {
        LOG.debug("preExecute IN op={} hasValidator={} hasContext={}", operation,
                operation.getValidator() != null, operation.getContext() != null);
        if (operation.getValidator() != null && object != null) {
            try {
                operation.getValidator().validate(object);
            } catch (ValidationException ve) {
                LOG.debug("preExecute op={} operation validator FAILED", operation);
                getContext().getEntity().getDao().detach(object); //Avoid flush of changes
                ve.setValidatedObject(object);
                throw ve;
            }
        }
        if (operation.getContext() != null) {
            operation.getContext().preExecute(object);
        }
        LOG.debug("preExecute OUT op={}", operation);
    }

    public void postExecute(Operation operation, Object object) throws PMException {
        LOG.debug("postExecute IN op={} hasContext={}", operation, operation.getContext() != null);
        if (operation.getContext() != null) {
            operation.getContext().postExecute(object);
        }
        LOG.debug("postExecute OUT op={}", operation);
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
