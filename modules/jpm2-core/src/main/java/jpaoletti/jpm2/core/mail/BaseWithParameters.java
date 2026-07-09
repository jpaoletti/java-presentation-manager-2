package jpaoletti.jpm2.core.mail;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class holding a String map of parameters with typed accessors.
 *
 * @author jpaoletti
 */
public class BaseWithParameters {

    protected Map<String, String> parameters = new LinkedHashMap<>();

    public BaseWithParameters() {
    }

    public BaseWithParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public BaseWithParameters with(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    public String getParameter(String name, String def) {
        final String config = getParameter(name);
        if (config == null) {
            return def;
        } else {
            return config;
        }
    }

    public String getParameter(String name) {
        if (getParameters() == null) {
            return null;
        }
        return parameters.get(name);
    }

    public Integer getParameter(String name, Integer def) {
        try {
            return Integer.parseInt(getParameter(name, (def == null) ? null : def.toString()));
        } catch (Exception exception) {
            return def;
        }
    }

    public Long getParameter(String name, Long def) {
        return Long.parseLong(getParameter(name, (def == null) ? null : def.toString()));
    }

    public boolean getParameter(String name, boolean def) {
        return Boolean.parseBoolean(getParameter(name, Boolean.toString(def)));
    }

}
