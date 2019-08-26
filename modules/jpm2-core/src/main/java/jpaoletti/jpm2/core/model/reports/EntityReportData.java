package jpaoletti.jpm2.core.model.reports;

import java.util.ArrayList;
import java.util.List;
import jpaoletti.jpm2.core.model.ListSort;

/**
 * Information used to generate a report based on user input.
 *
 * @author jpaoletti
 */
public class EntityReportData {

    private Integer max;
    private Integer from;
    private String tabField;
    private String sortField;
    private ListSort.SortDirection sortDirection = ListSort.SortDirection.ASC;
    private List<EntityReportDataFilter> filters = new ArrayList<>();
    private List<EntityReportDataFormula> formulas = new ArrayList<>();
    private List<EntityReportDataGraphic> graphics = new ArrayList<>();
    private List<String> groups = new ArrayList<>();
    private List<String> visibleFields = new ArrayList<>();

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public List<EntityReportDataFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<EntityReportDataFilter> filters) {
        this.filters = filters;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<EntityReportDataFormula> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<EntityReportDataFormula> formulas) {
        this.formulas = formulas;
    }

    public List<EntityReportDataGraphic> getGraphics() {
        return graphics;
    }

    public void setGraphics(List<EntityReportDataGraphic> graphics) {
        this.graphics = graphics;
    }

    public String getTabField() {
        return tabField;
    }

    public void setTabField(String tabField) {
        this.tabField = tabField;
    }

    public List<String> getVisibleFields() {
        return visibleFields;
    }

    public void setVisibleFields(List<String> visibleFields) {
        this.visibleFields = visibleFields;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public ListSort.SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(ListSort.SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

}
