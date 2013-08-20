package jpaoletti.jpm2.core.converter;

import java.util.Properties;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.util.JPMUtils;

/**
 * A converter is an object associated to a field that determine the way the
 * field value will be visualized and build from a visual representation to a
 * value for a given operation.
 *
 * @author jpaoletti
 *
 */
public class Converter extends PMCoreObject {

    private Properties properties;

    /**
     * This method transforms the given value into an object to visualize it
     *
     * @return The string representation of the object
     * @throws ConverterException
     */
    public Object visualize(Field field, Object object) throws ConverterException {
        throw new IgnoreConvertionException();
    }

    /**
     * This method takes a specific format of the object from the visualization
     * (usually a string) and transforms it in the required object. TODO
     *
     * @return The value to be set in the entity instance.
     * @throws ConverterException
     *
     */
    public Object build(Field field, Object newValue) throws ConverterException {
        throw new IgnoreConvertionException();
    }

    /**
     * Getter for a specific property with a default value in case its not
     * defined. Only works for string.
     *
     * @param name Property name
     * @param def Default value
     * @return Property value only if its a string
     */
    public String getConfig(String name, String def) {
        debug("Converter.getConfig(" + name + "," + def + ")");
        if (properties != null) {
            Object obj = properties.get(name);
            if (obj instanceof String) {
                return obj.toString();
            }
        }
        return def;
    }

    /**
     * Getter for any property in the properties object
     *
     * @param name The property name
     * @return The property value
     */
    public String getConfig(String name) {
        return getConfig(name, null);
    }

    /**
     * Getter for the value
     *
     * @param einstance The entity instance
     * @param field The field
     * @return The field value on the entity instance
     */
    protected Object getValue(Object einstance, Field field) {
        return getValue(einstance, field.getProperty());
    }

    /**
     * Getter for a nested property in the given object.
     *
     * @param obj The object
     * @param propertyName The name of the property to get
     * @return The property value
     */
    public Object getValue(Object obj, String propertyName) {
        if (obj != null && propertyName != null) {
            return JPMUtils.get(obj, propertyName);
        }
        return null;
    }

    /**
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
