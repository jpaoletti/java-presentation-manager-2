package jpaoletti.jpm2.core.model.reports;

/**
 *
 * @author jpaoletti
 */
public enum EntityReportGraficType {
    Pie(1), Line(10);
    private Integer maxSeries;

    private EntityReportGraficType(Integer maxSeries) {
        this.maxSeries = maxSeries;
    }

    public Integer getMaxSeries() {
        return maxSeries;
    }

    public void setMaxSeries(Integer maxSeries) {
        this.maxSeries = maxSeries;
    }

}
