package jpaoletti.jpm2.core.model.reports;

import java.util.List;

/**
 *
 * @author jpaoletti
 */
public class EntityReportResult {

    private List mainData;
    private List<List> graphicData;

    public EntityReportResult(List mainData, List<List> graphic) {
        this.mainData = mainData;
        this.graphicData = graphic;
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

}
