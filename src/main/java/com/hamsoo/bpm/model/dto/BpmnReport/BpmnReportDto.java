package com.hamsoo.bpm.model.dto.BpmnReport;

public class BpmnReportDto {

    private  String gateWayName;
    private  String modelName;
    private  String laneName;
    private  String laneDecision;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getLaneName() {
        return laneName;
    }

    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    public String getLaneDecision() {
        return laneDecision;
    }

    public void setLaneDecision(String laneDecision) {
        this.laneDecision = laneDecision;
    }


    public String getGateWayName() {
        return gateWayName;
    }

    public void setGateWayName(String gateWayName) {
        this.gateWayName = gateWayName;
    }
}
