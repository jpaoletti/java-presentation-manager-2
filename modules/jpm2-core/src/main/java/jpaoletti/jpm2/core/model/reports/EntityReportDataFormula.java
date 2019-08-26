package jpaoletti.jpm2.core.model.reports;

/**
 *
 * @author jpaoletti
 */
public class EntityReportDataFormula {

    private EntityReportDataFormulas formula;
    private String field;

    public EntityReportDataFormulas getFormula() {
        return formula;
    }

    public void setFormula(EntityReportDataFormulas formula) {
        this.formula = formula;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
