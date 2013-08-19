package jpaoletti.jpm2.core.model;

import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.converter.Converter;

/**
 * A field configuration item for one or more operations. Works also as a
 * shorcut for econverters.
 *
 * @author jpaoletti
 */
public class FieldConfig extends PMCoreObject {

    public static final String ALL = "all";
    private String operations;
    private String perm;
    private Converter converter;

    //TODO private List<FieldValidator> validators;
    public FieldConfig() {
    }

    public FieldConfig(String operations, String perm, Converter converter) {
        this.operations = operations;
        this.perm = perm;
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

    public String getPerm() {
        if (perm == null) {
            return ALL;
        } else {
            return perm.trim();
        }
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    boolean match(Operation operation) {
        return includes(operation.getId()) && (getPerm().equalsIgnoreCase(ALL) || userHasRole(getPerm()));
    }
}
