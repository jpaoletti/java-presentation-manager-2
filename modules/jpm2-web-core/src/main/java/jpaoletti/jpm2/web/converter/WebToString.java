package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.ToStringConverter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author jpaoletti
 */
public class WebToString extends ToStringConverter {

    private boolean useTitle = true;
    /**
     * When true the value is rendered as html (not escaped). Use it only for
     * fields that store trusted html on purpose.
     */
    private boolean html = false;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object value, String instanceId) throws ConverterException, ConfigurationException {
        return wrap(field, escape(process(value), value), value);
    }

    /**
     * Wraps an already rendered (and escaped when needed) content. The title
     * is always escaped.
     */
    public String wrap(Field field, String process, Object value) {
        final String originalValue = (value == null) ? getNullValue() : getFinalValue(value, getProperties());
        return "<span class='to-string' title='" + (isUseTitle() ? escapeAttribute(originalValue, value) : "") + "' data-align='" + field.getAlign() + "'>" + process + "</span>";
    }

    /**
     * Escapes a value for html content, unless html is true.
     */
    protected String escape(String value) {
        return (isHtml() || value == null) ? value : StringEscapeUtils.escapeHtml4(value);
    }

    /**
     * Escapes the rendered text of a value, unless the value is trusted (see
     * {@link #isTrusted(Object)}).
     */
    protected String escape(String text, Object value) {
        return isTrusted(value) ? text : escape(text);
    }

    /**
     * Escapes the rendered text of a value for an html attribute. Trusted
     * values keep their entities (so they are decoded by the browser) and only
     * the quotes are escaped.
     */
    public static String escapeAttribute(String text, Object value) {
        if (isTrusted(value)) {
            return text == null ? null : text.replace("\"", "&quot;").replace("'", "&#39;");
        }
        return escapeAttribute(text);
    }

    /**
     * Values defined in the code (enums) are trusted: their texts may contain
     * html entities, like "Restituci&amp;oacute;n", following the same convention
     * as the jsp pages.
     */
    public static boolean isTrusted(Object value) {
        return value instanceof Enum;
    }

    /**
     * Escapes a value for an html attribute (single or double quoted).
     */
    public static String escapeAttribute(String value) {
        return value == null ? null : StringEscapeUtils.escapeHtml4(value).replace("'", "&#39;");
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    public boolean isUseTitle() {
        return useTitle;
    }

    public void setUseTitle(boolean useTitle) {
        this.useTitle = useTitle;
    }

}
