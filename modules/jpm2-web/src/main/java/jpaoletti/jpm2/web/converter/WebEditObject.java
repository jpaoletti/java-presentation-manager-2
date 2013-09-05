package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
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

    public WebEditObject() {
        this.pageSize = 10;
        this.minSearch = 0;
    }

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {
        final Object value = (object == null) ? null : getValue(object, field);
        final StringBuilder sb = new StringBuilder("@page:object-converter.jsp");
        sb.append("?entityId=").append(getEntity().getId());
        sb.append("&textField=").append(getTextField());
        sb.append("&pageSize=").append(getPageSize());
        sb.append("&minSearch=").append(getMinSearch());
        if (value != null) {
            sb.append("&value=").append(getEntity().getDao().getId(value));
        }
        if (getFilter() != null) {
            sb.append("&filter=").append(getFilter().getId());
        }
        return sb.toString();
    }

    @Override
    public Object build(Field field, Object object, Object newValue) throws ConverterException {
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
}
