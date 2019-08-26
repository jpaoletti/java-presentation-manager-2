package jpaoletti.jpm2.core.model.reports;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jpaoletti
 */
public class EntityReportDataGraphic {

    private String title;
    private EntityReportGraficType type;
    private String groupField;
    private List<EntityReportDataFormula> formulas = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EntityReportGraficType getType() {
        return type;
    }

    public void setType(EntityReportGraficType type) {
        this.type = type;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public List<EntityReportDataFormula> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<EntityReportDataFormula> formulas) {
        this.formulas = formulas;
    }

}
