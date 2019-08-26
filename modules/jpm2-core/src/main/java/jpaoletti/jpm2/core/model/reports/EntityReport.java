package jpaoletti.jpm2.core.model.reports;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jpaoletti.jpm2.core.PMCoreObject;
import jpaoletti.jpm2.core.model.Entity;
import org.apache.commons.lang.StringUtils;

/**
 * An Entity Report is configuration class based on an Entity that provides
 * generic reporting based on the entity.
 *
 * @author jpaoletti
 */
public class EntityReport extends PMCoreObject {

    private Entity entity; //Base entity for reporting.

    private String title;

    private String filteringFields; //space separated list of field ids that can be used for filtering. 

    private String groupableFields; //Space separated list of field ids that can be used as group by

    private String numericFields; //space separated list of field ids that can be operated as numbers for SUM, AVG and so on.

    private String tabbableFields; //space separated list of field ids that can be used to organize data in tabs

    private boolean allowDetail = true;

    private EntityReportData fixedData;

    private List<EntityReportDescriptiveField> descriptiveFields; //Space separated list of field ids that can be shown as informative data on linear reports (without grouping)

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<EntityReportDescriptiveField> getDescriptiveFields() {
        return descriptiveFields;
    }

    public void setDescriptiveFields(List<EntityReportDescriptiveField> descriptiveFields) {
        this.descriptiveFields = descriptiveFields;
    }

    public String getGroupableFields() {
        return groupableFields;
    }

    public void setGroupableFields(String groupableFields) {
        this.groupableFields = groupableFields;
    }

    public String getNumericFields() {
        return numericFields;
    }

    public void setNumericFields(String numericFields) {
        this.numericFields = numericFields;
    }

    public String getFilteringFields() {
        return filteringFields;
    }

    public void setFilteringFields(String filteringFields) {
        this.filteringFields = filteringFields;
    }

    public List<String> getDescriptiveFieldList() {
        return getDescriptiveFields().stream().map(EntityReportDescriptiveField::getField).collect(Collectors.toList());
    }

    public List<String> getTabbableFieldList() {
        return Arrays.asList(StringUtils.split(getTabbableFields(), ' '));
    }

    public List<String> getGroupableFieldList() {
        return Arrays.asList(StringUtils.split(getGroupableFields(), ' '));
    }

    public List<String> getNumericFieldList() {
        if (getNumericFields() == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(StringUtils.split(getNumericFields(), ' '));
    }

    public List<String> getFilteringFieldList() {
        return Arrays.asList(StringUtils.split(getFilteringFields(), ' '));
    }

    public List<String> getSortableFieldList() {
        return getDescriptiveFields().stream()
                .filter(EntityReportDescriptiveField::isSortable)
                .map(EntityReportDescriptiveField::getField)
                .collect(Collectors.toList());
    }

    public EntityReportDescriptiveField getDescriptiveField(String fieldId) {
        return getDescriptiveFields().stream().filter(f -> f.getField().equalsIgnoreCase(fieldId)).findFirst().orElse(null);
    }

    public String getTabbableFields() {
        return tabbableFields;
    }

    public void setTabbableFields(String tabbableFields) {
        this.tabbableFields = tabbableFields;
    }

    public boolean isAllowDetail() {
        return allowDetail;
    }

    public void setAllowDetail(boolean allowDetail) {
        this.allowDetail = allowDetail;
    }

    public EntityReportData getFixedData() {
        return fixedData;
    }

    public void setFixedData(EntityReportData fixedData) {
        this.fixedData = fixedData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
