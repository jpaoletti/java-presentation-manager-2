package jpaoletti.jpm2.core.model.reports;

import jpaoletti.jpm2.core.converter.Converter;

/**
 *
 * @author jpaoletti
 */
public class EntityReportDescriptiveField {

    private String field;
    private Converter converter;
    private boolean visible = true; //by default
    private boolean sortable = true;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

}
