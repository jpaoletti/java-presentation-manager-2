package jpaoletti.jpm2.core.converter;

import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.exception.ConfigurationException;
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

    /**
     * This method transforms the given value into an object to visualize it
     *
     * @param field
     * @param object
     * @param instanceId
     * @return The string representation of the object
     * @throws ConverterException
     * @throws jpaoletti.jpm2.core.exception.ConfigurationException
     */
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        throw new IgnoreConvertionException();
    }

    /**
     * This method takes a specific format of the object from the visualization
     * (usually a string) and transforms it in the required object. TODO
     *
     * @param field
     * @param object
     * @param newValue
     * @return The value to be set in the entity instance.
     * @throws ConverterException
     * @throws jpaoletti.jpm2.core.exception.ConfigurationException
     *
     */
    public Object build(Field field, Object object, Object newValue) throws ConverterException, ConfigurationException {
        throw new IgnoreConvertionException();
    }

    /**
     * Getter for the value
     *
     * @param einstance The entity instance
     * @param field The field
     * @return The field value on the entity instance
     * @throws jpaoletti.jpm2.core.exception.ConfigurationException
     */
    protected Object getValue(Object einstance, Field field) throws ConfigurationException {
        return getValue(einstance, field.getProperty());
    }

    /**
     * Getter for a nested property in the given object.
     *
     * @param obj The object
     * @param propertyName The name of the property to get
     * @return The property value
     * @throws jpaoletti.jpm2.core.exception.ConfigurationException
     */
    public Object getValue(Object obj, String propertyName) throws ConfigurationException {
        if (obj != null && propertyName != null) {
            return JPMUtils.get(obj, propertyName);
        }
        return null;
    }
}
