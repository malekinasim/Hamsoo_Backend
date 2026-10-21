package com.hamsoo.bpm.model.dto.approverlog;

import java.time.LocalDateTime;

public class ApproverLogDto {

    private Long id;
    private LocalDateTime stateDate;
    private String approveStateCode;
    private String approveStateTitle;
    private Long modelApproverId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStateDate() {
        return stateDate;
    }

    public void setStateDate(LocalDateTime stateDate) {
        this.stateDate = stateDate;
    }

    public String getApproveStateCode() {
        return approveStateCode;
    }

    public void setApproveStateCode(String approveStateCode) {
        this.approveStateCode = approveStateCode;
    }

    public String getApproveStateTitle() {
        return approveStateTitle;
    }

    public void setApproveStateTitle(String approveStateTitle) {
        this.approveStateTitle = approveStateTitle;
    }


    public Long getModelApproverId() {
        return modelApproverId;
    }

    public void setModelApproverId(Long modelApproverId) {
        this.modelApproverId = modelApproverId;
    }
}
