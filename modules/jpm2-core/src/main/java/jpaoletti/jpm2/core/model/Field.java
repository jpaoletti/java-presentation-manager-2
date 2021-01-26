package jpaoletti.jpm2.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.search.Searcher;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A Field represents a property of the represented entity.
 *
 * @author jpaoletti
 *
 */
public class Field extends PMCoreObject {

    private String id;
    @Autowired
    @Qualifier("default-converter")
    private Converter defaultConverter;
    /**
     * The property to be accesed on the entity instance objects. There must be
     * a getter and a setter for this name on the represented entity. When null,
     * default is the field id
     */
    private String property;
    private String width;
    private String display;
    private boolean displayTitle = true;
    private String auth;
    private String defaultValue;
    private String align; //left right center, TODO
    private Searcher searcher;
    private boolean sortable;
    private List<FieldConfig> configs;

    /**
     * Default constructor
     */
    public Field() {
        super();
        this.defaultValue = "";
        this.sortable = true;
    }

    public Field(String id) {
        super();
        this.defaultValue = "";
        this.sortable = true;
        this.id = id;
    }

    /**
     * Return the default converter if none is defined
     *
     * @return The converter
     */
    public Converter getDefaultConverter() {
        return defaultConverter;
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    public List<FieldConfig> getConfigs() {
        if (configs == null) {
            configs = new ArrayList<>();
            configs.add(new FieldConfig(FieldConfig.ALL, null, getDefaultConverter()));
        }
        return configs;
    }

    public void setConfigs(List<FieldConfig> configs) {
        this.configs = configs;
    }

    /**
     * A (separated by blanks) list of operation ids where this field will be
     * displayed
     *
     * @return The list
     */
    public String getDisplay() {
        if (display == null || display.trim().equals("")) {
            return "all";
        }
        return display;
    }

    /**
     * Indicates if the field is shown in the given operation id
     *
     * @param operationId The Operation id
     * @return true if field is displayed on the operation
     */
    public boolean shouldDisplay(String operationId) {
        if (operationId == null) {
            return false;
        }
        //First we check permissions
        try {
            checkAuthorization();
        } catch (NotAuthorizedException ex) {
            return false;
        }
        for (FieldConfig config : getConfigs()) {
            if (config.includes(operationId)) {
                try {
                    config.checkAuthorization();
                    return true;
                } catch (NotAuthorizedException ex) {
                }
            }
        }
        if (getDisplay().equalsIgnoreCase("all")) {
            return true;
        }
        final String[] split = getDisplay().split("[ ]");
        for (String string : split) {
            if (string.equalsIgnoreCase(operationId)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     *
     * @return
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     *
     * @param align
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     *
     * @return
     */
    public String getAlign() {
        return align;
    }

    /**
     *
     * @param width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     *
     * @return
     */
    public String getWidth() {
        if (width == null) {
            return "";
        }
        return width;
    }

    /**
     * String representation of the field
     *
     * @return
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * @return the property of the entity instance object that can be accesed by
     * getter and setter. Default value is the field id
     */
    public String getProperty() {
        String r = property;
        if (r == null || r.trim().equals("")) {
            r = id;
        }
        return r;
    }

    /**
     * Setter for property
     *
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    public void setDefaultConverter(Converter defaultConverter) {
        this.defaultConverter = defaultConverter;
    }

    public Converter getConverter(EntityInstance instance, Operation operation) throws PMException {
        for (FieldConfig fieldConfig : getConfigs()) {
            if (fieldConfig.match(instance, operation)) {
                return fieldConfig.getConverter();
            }
        }
        return null;
    }

    public List<FieldValidator> getValidators(EntityInstance instance, Operation operation) throws PMException {
        for (FieldConfig fieldConfig : getConfigs()) {
            if (fieldConfig.match(instance, operation)) {
                if (fieldConfig.getValidators() == null || fieldConfig.getValidators().isEmpty()) {
                    if (fieldConfig.getValidator() == null) {
                        return Collections.EMPTY_LIST;
                    } else {
                        return Arrays.asList(fieldConfig.getValidator());
                    }
                } else {
                    return fieldConfig.getValidators();
                }
            }
        }
        return null;
    }

    public Searcher getSearcher() {
        return searcher;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    /**
     * Finds the current field class.
     *
     * @param baseClass
     * @return
     */
    public Class getClass(String baseClass) {
        try {
            final String _property = getProperty();
            final String[] _properties = _property.split("[.]");
            Class<?> clazz = Class.forName(baseClass);
            for (int i = 0; i < _properties.length - 1; i++) {
                clazz = FieldUtils.getField(clazz, _properties[i], true).getType();
            }
            final String className = FieldUtils.getField(clazz, _properties[_properties.length - 1], true).getType().getName();
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            JPMUtils.getLogger().error(ex);
        }
        return null;
    }

    public String getTitle(final Entity entity) {
        return getMessage("jpm.field." + entity.getId() + "." + getId());
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Field other = (Field) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    @Override
    public String getAuth() {
        return this.auth;
    }

    public boolean isDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(boolean displayTitle) {
        this.displayTitle = displayTitle;
    }

    public String getAuthKey(Entity entity, EntityContext context, Operation operation) {
        final String eid = entity.getId() + (context == null ? "" : "." + context.getId());
        return String.format("jpm.auth.field.%s.%s.%s", eid, operation.getId(), getId());
    }
}
