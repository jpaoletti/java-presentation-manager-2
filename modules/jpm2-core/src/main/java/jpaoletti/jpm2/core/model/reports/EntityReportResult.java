package jpaoletti.jpm2.core.model.reports;

import java.util.List;

/**
 *
 * @author jpaoletti
 */
public class EntityReportResult {

    private List mainData;
    private List<List> graphicData;
    private List<String> visibleFields;

    public EntityReportResult(List mainData, List<List> graphicData, List<String> visibleFields) {
        this.mainData = mainData;
        this.graphicData = graphicData;
        this.visibleFields = visibleFields;
    }

    public List getMainData() {
        return mainData;
    }

    public void setMainData(List mainData) {
        this.mainData = mainData;
    }

    public List<List> getGraphicData() {
        return graphicData;
    }

    public void setGraphicData(List<List> graphicData) {
        this.graphicData = graphicData;
    }

    public List<String> getVisibleFields() {
        return visibleFields;
    }

    public void setVisibleFields(List<String> visibleFields) {
        this.visibleFields = visibleFields;
    }

}
