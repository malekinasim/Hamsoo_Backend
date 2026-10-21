package com.hamsoo.bpm.model.entity.modelapprover;

import com.hamsoo.bpm.model.entity.approverlog.ApproverLog;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.enums.Status;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "bpm_model_approvers", schema = "bpm", catalog = "BPM_DB")
public class ModelApprover extends AuditEntity<Long> {
    private Long id;
    private int order;
    private String status = Status.ACTIVE.name();
    private CodeCategory approveGroup;
    private CodeCategory level;
    private List<ApproverLog> ApproverLogs;
    private ModelMemberRole modelMemberRole;
    private Boolean deleteable = true;
    private String responseDescription;
    private String refrenceDescription;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approver_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "approve_order", nullable = false)
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    @Basic
    @Column(name = "response_description")
    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    @Basic
    @Column(name = "refrence_description")
    public String getRefrenceDescription() {
        return refrenceDescription;
    }

    public void setRefrenceDescription(String refrenceDescription) {
        this.refrenceDescription = refrenceDescription;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approve_level", nullable = false)
    public CodeCategory getLevel() {
        return level;
    }

    public void setLevel(CodeCategory level) {
        this.level = level;
    }

    @OneToMany(mappedBy = "modelApprover")
    public List<ApproverLog> getApproverLogs() {
        return ApproverLogs;
    }

    public void setApproverLogs(List<ApproverLog> ApproverLogs) {
        this.ApproverLogs = ApproverLogs;
    }

    @ManyToOne
    @JoinColumn(name = "model_member_id", nullable = false)
    public ModelMemberRole getModelMemberRole() {
        return modelMemberRole;
    }

    public void setModelMemberRole(ModelMemberRole modelMemberRole) {
        this.modelMemberRole = modelMemberRole;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approve_group_id", referencedColumnName = "code_category_id", nullable = false)
    public CodeCategory getApproveGroup() {
        return approveGroup;
    }

    public void setApproveGroup(CodeCategory approveGroup) {
        this.approveGroup = approveGroup;
    }

    @Basic
    @Column(name = "deleteable", nullable = false)
    public Boolean getDeleteable() {
        return deleteable;
    }

    public void setDeleteable(Boolean deleteable) {
        this.deleteable = deleteable;
    }

    public enum ApproveLevelType {
        APPROVE("APPROVE"), FYI("FYI");
        public static final String CATEGORY = "APPROVE_LEVEL_TYPE";
        private String code;

        ApproveLevelType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum ApproveGroupType {
        OWNERSGROUP("OWNERSGROUP"),
        SUPPLIERSGROUP("SUPPLIERSGROUP"),
        CUSTOMERSGROUP("CUSTOMERSGROUP"),
        OTHERS("OTHERS");

        public static final String CATEGORY = "APPROVE_GROUP_TYPE";
        private String code;

        ApproveGroupType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
