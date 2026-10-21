package com.hamsoo.bpm.model.dto.modelapprover;

public class ModelApproverChangeOrderRQ {
    private Long modelVersionId;
    private Long taregetModelApprover;
    private Long srcModelApprover;

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public Long getTaregetModelApprover() {
        return taregetModelApprover;
    }

    public void setTaregetModelApprover(Long taregetModelApprover) {
        this.taregetModelApprover = taregetModelApprover;
    }

    public Long getSrcModelApprover() {
        return srcModelApprover;
    }

    public void setSrcModelApprover(Long srcModelApprover) {
        this.srcModelApprover = srcModelApprover;
    }
}
