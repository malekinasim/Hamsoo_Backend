package com.hamsoo.bpm.model.entity.approverlog;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.enums.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_approver_logs", schema = "bpm", catalog = "BPM_DB")
public class ApproverLog extends AuditEntity<Long> {
    private Long id;
    private LocalDateTime stateDate;
    private CodeCategory approveState;
    private ModelApprover modelApprover;
    private String status = Status.ACTIVE.name();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approver_log_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "state_date")
    public LocalDateTime getStateDate() {
        return stateDate;
    }

    public void setStateDate(LocalDateTime stateDate) {
        this.stateDate = stateDate;
    }

    @Basic
    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state", referencedColumnName = "code_category_id", nullable = false)
    public CodeCategory getApproveState() {
        return approveState;
    }

    public void setApproveState(CodeCategory approveState) {
        this.approveState = approveState;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_approver_id", referencedColumnName = "approver_id", nullable = false)
    public ModelApprover getModelApprover() {
        return modelApprover;
    }

    public void setModelApprover(ModelApprover modelApprover) {
        this.modelApprover = modelApprover;
    }


    public enum ApproveLogState {
        FORYOURINFO("FOR_YOUR_INFORMATION"),
        FORYOURAPPROVE("FOR_APPROVE"),
        ACKNOWLEDGE("ACKNOWLEDGE"),
        APPROVE("APPROVE"),
        REJECT("REJECT");

        public static final String CATEGORY = "APPROVE_LOG_STATE";
        private String code;


        ApproveLogState(String code) {
            this.code = code;
        }


        public String getCode() {
            return code;
        }
    }
}
