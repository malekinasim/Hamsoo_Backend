package com.hamsoo.bpm.model.entity.modelmemberrole;

import com.hamsoo.bpm.model.entity.modelapprover.ModelApprover;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.modelmember.ModelMember;
import com.hamsoo.bpm.model.entity.modelrole.ModelRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "bpm_model_member_roles", schema = "bpm", catalog = "BPM_DB")
public class ModelMemberRole extends AuditEntity<Long> {
    private Long id;
    private ModelRole modelRole;
    private ModelMember modelMember;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String status = Status.ACTIVE.name();
    private List<ModelApprover> modelApprovers;


    @Id
    @Override
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_member_role_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "from_date", nullable = false)
    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    @Basic
    @Column(name = "to_date", nullable = false)
    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    @Basic
    @Column(name = "status", nullable = false, length = -1)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_role_id", nullable = false)
    public ModelRole getModelRole() {
        return modelRole;
    }

    public void setModelRole(ModelRole modelRole) {
        this.modelRole = modelRole;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_member_id", nullable = false)
    public ModelMember getModelMember() {
        return modelMember;
    }

    public void setModelMember(ModelMember modelMember) {
        this.modelMember = modelMember;
    }


    @OneToMany(mappedBy = "modelMemberRole", targetEntity = ModelApprover.class, fetch = FetchType.LAZY)
    public List<ModelApprover> getModelApprovers() {
        return modelApprovers;
    }

    public void setModelApprovers(List<ModelApprover> modelApprovers) {
        this.modelApprovers = modelApprovers;
    }


}
