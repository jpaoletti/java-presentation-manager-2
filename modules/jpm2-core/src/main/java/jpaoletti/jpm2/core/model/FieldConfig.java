package jpaoletti.jpm2.core.model;

import java.util.List;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.security.SecurityUtils;

/**
 * A field configuration item for one or more operations. Works also as a
 * shorcut for econverters.
 *
 * @author jpaoletti
 */
public class FieldConfig extends PMCoreObject {

    public static final String ALL = "all";
    private String operations;
    private String auth;
    private Converter converter;
    private FieldConfigCondition condition;
    private FieldValidator validator;
    private List<FieldValidator> validators;

    public FieldConfig() {
    }

    public FieldConfig(String operations, String auth, Converter converter) {
        this.operations = operations;
        this.auth = auth;
        this.converter = converter;
    }

    public boolean includes(String operationId) {
        if (getOperations() == null) {
            return true;
        }
        if (getOperations().equalsIgnoreCase(ALL)) {
            return true;
        }
        final String[] split = getOperations().split("[ ]");
        for (String string : split) {
            if (string.equalsIgnoreCase(operationId)) {
                return true;
            }
        }
        return false;
    }

    public String getOperations() {
        if (operations == null) {
            return ALL;
        } else {
            return operations.trim();
        }
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    @Override
    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    boolean match(EntityInstance instance, Operation operation) throws PMException {
        if (operation != null) {
            final boolean prevalidation = includes(operation.getId()) && (getAuth() == null || SecurityUtils.userHasRole(getAuth()));
            if (prevalidation && getCondition() != null && instance != null) {
                return getCondition().check(instance, operation);
            }
            return prevalidation;
        } else {
            return false;
        }
    }

    public FieldValidator getValidator() {
        return validator;
    }

    public void setValidator(FieldValidator validator) {
        this.validator = validator;
    }

    public List<FieldValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<FieldValidator> validators) {
        this.validators = validators;
    }

    public FieldConfigCondition getCondition() {
        return condition;
    }

    public void setCondition(FieldConfigCondition condition) {
        this.condition = condition;
    }

}
