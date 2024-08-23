package jpaoletti.jpm2.web.converter;

import java.io.Serializable;
import jpaoletti.jpm2.core.JPMContext;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.exception.OperationNotFoundException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.ListFilter;
import jpaoletti.jpm2.core.model.Operation;
import jpaoletti.jpm2.web.ObjectConverterData;
import org.apache.commons.lang3.StringUtils;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jpaoletti
 */
public class WebEditObject extends Converter {

    private Entity entity;
    private ListFilter filter;
    private String textField;
    private String textFieldDetails;
    private String textFieldDetailsOperation;
    private String textFieldDetailsEntity;
    private String entityContext;
    private Integer pageSize;
    private Integer minSearch;
    private String placeHolder;
    private String related;
    private String sortBy; //field id
    private boolean readonly;
    private boolean addable;

    @Autowired
    private JPMContext context;

    public WebEditObject() {
        this.pageSize = 10;
        this.minSearch = 0;
        this.placeHolder = "...";
        this.readonly = false;
        this.addable = false;
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
        sb.append("&addable=").append(isAddable());
        sb.append("&currentId=").append(instanceId);
        sb.append("&sortBy=").append(getSortBy() == null ? "" : getSortBy());
        if (value != null) {
            final ObjectConverterData.ObjectConverterDataItem data = ObjectConverterData.buildDataObject(getTextField(), getEntity(), null, String.valueOf(getEntity().getDao().getId(value)), value);
            sb.append("&value=").append(data.getId());
            sb.append("&valueText=").append(Util.URLEncode(data.getText(), "UTF-8"));
        }
        if (getFilter() != null) {
            sb.append("&filter=").append(getFilter().getId());
        }
        if (getRelated() != null) {
            sb.append("&related=").append(getRelated());
        }
        if (StringUtils.isNotEmpty(textFieldDetails)) {
            sb.append("&textFieldDetails=").append(textFieldDetails);
            String operationLink = "";
            String operationTitle = "";
            String operationIcon = "";
            if (StringUtils.isNotEmpty(textFieldDetailsOperation) || getAuthorizationService().userHasRole(textFieldDetailsOperation)) {
                try {
                    final Serializable localId = getEntity().getDao(getContext().getEntityContext()).getId(value);
                    final Operation op = getEntity().getOperation(textFieldDetailsOperation, getContext().getContext());
                    operationTitle = getMessage(op.getTitle(), getMessage(getEntity().getTitle()));
                    operationIcon = op.getIcon();
                    final String entityId
                            = StringUtils.isNotEmpty(textFieldDetailsEntity) ? textFieldDetailsEntity
                            : getEntity().getId() + ((getEntityContext() == null) ? "" : (PresentationManager.CONTEXT_SEPARATOR + getEntityContext()));
                    switch (op.getScope()) {
                        case ITEM:
                            operationLink = "jpm/" + entityId + "/" + localId + "/" + op.getPathId();
                            break;
                        default:
                            operationLink = "jpm/" + entityId + "/" + op.getPathId();
                            break;
                    }
                    sb.append("&textFieldDetailsOperation=").append(textFieldDetailsOperation);
                    sb.append("&operationLink=").append(operationLink);
                    sb.append("&operationIcon=").append(operationIcon);
                    sb.append("&operationTitle=").append(operationTitle);
                } catch (NotAuthorizedException | OperationNotFoundException ex) {
                    //We don't care for now
                }
            }
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

    public boolean isAddable() {
        return addable;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    public String getTextFieldDetails() {
        return textFieldDetails;
    }

    public void setTextFieldDetails(String textFieldDetails) {
        this.textFieldDetails = textFieldDetails;
    }

    public String getTextFieldDetailsOperation() {
        return textFieldDetailsOperation;
    }

    public void setTextFieldDetailsOperation(String textFieldDetailsOperation) {
        this.textFieldDetailsOperation = textFieldDetailsOperation;
    }

    public JPMContext getContext() {
        return context;
    }

    public void setContext(JPMContext context) {
        this.context = context;
    }

    public String getEntityContext() {
        return entityContext;
    }

    public void setEntityContext(String entityContext) {
        this.entityContext = entityContext;
    }

    public String getTextFieldDetailsEntity() {
        return textFieldDetailsEntity;
    }

    public void setTextFieldDetailsEntity(String textFieldDetailsEntity) {
        this.textFieldDetailsEntity = textFieldDetailsEntity;
    }
}
