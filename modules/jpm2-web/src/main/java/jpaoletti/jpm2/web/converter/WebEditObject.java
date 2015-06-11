package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.ListFilter;

/**
 *
 * @author jpaoletti
 */
public class WebEditObject extends Converter {

    private Entity entity;
    private ListFilter filter;
    private String textField;
    private Integer pageSize;
    private Integer minSearch;
    private String placeHolder;
    private String related;
    private String sortBy; //field id
    private boolean readonly;

    public WebEditObject() {
        this.pageSize = 10;
        this.minSearch = 0;
        this.placeHolder = "...";
        this.readonly = false;
    }

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final Object value = (object == null) ? null : getValue(object, field);
        final StringBuilder sb = new StringBuilder("@page:object-converter.jsp");
        sb.append("?entityId=").append(getEntity().getId());
        sb.append("&textField=").append(getTextField());
        sb.append("&pageSize=").append(getPageSize());
        sb.append("&minSearch=").append(getMinSearch());
        sb.append("&readonly=").append(isReadonly());
        sb.append("&sortBy=").append(getSortBy() == null ? "" : getSortBy());
        if (value != null) {
            sb.append("&value=").append(getEntity().getDao().getId(value));
        }
        if (getFilter() != null) {
            sb.append("&filter=").append(getFilter().getId());
        }
        if (getRelated() != null) {
            sb.append("&related=").append(getRelated());
        }
        sb.append("&placeHolder=").append(getPlaceHolder());
        return sb.toString();
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (isReadonly()) {
            throw new IgnoreConvertionException("Readonly");
        }
        if (newValue == null || "".equals(newValue)) {
            return null;
        } else {
            return getEntity().getDao().get((String) newValue);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getMinSearch() {
        return minSearch;
    }

    public void setMinSearch(Integer minSearch) {
        this.minSearch = minSearch;
    }

    public ListFilter getFilter() {
        return filter;
    }

    public void setFilter(ListFilter filter) {
        this.filter = filter;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
