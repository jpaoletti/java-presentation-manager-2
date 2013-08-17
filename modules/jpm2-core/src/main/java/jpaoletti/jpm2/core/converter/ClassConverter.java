package jpaoletti.jpm2.core.converter;

/**
 * A class converter is a vinculation of a converter and a java class so that
 * the default converter for the class is the given external converter.
 *
 * @author jpaoletti
 */
public class ClassConverter {

    private Converter converter;
    private String className;
    private String operations;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }
}
