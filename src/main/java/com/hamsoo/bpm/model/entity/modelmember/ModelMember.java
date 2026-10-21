package com.hamsoo.bpm.model.entity.modelmember;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.modelmemberrole.ModelMemberRole;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bpm_model_members", schema = "bpm", catalog = "BPM_DB")
public class ModelMember extends AuditEntity<Long> {

    private Long id;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String status = Status.ACTIVE.name();
    private ModelVersion modelVersion;
    //    private User user;
    private Long memberId;
    private CodeCategory memberType;

    private List<ModelMemberRole> modelMemberRoleList;

    public ModelMember() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_member_id", nullable = false)
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
    @Column(name = "to_date", nullable = true)
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
    @JoinColumn(name = "model_id", nullable = false)
    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

    @Basic
    @Column(name = "member_id", nullable = false, length = -1)
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_type", referencedColumnName = "code_category_id", nullable = false)
    public CodeCategory getMemberType() {
        return memberType;
    }

    public void setMemberType(CodeCategory memberType) {
        this.memberType = memberType;
    }

    @OneToMany(mappedBy = "modelMember", fetch = FetchType.LAZY)
    public List<ModelMemberRole> getModelMemberRoleList() {
        return modelMemberRoleList;
    }

    public void setModelMemberRoleList(List<ModelMemberRole> modelMemberRoleList) {
        this.modelMemberRoleList = modelMemberRoleList;
    }


    public enum MemberType {
        USER("USER"),
        POST("POST"),
        JOB("JOB"),
        UNIT("UNIT");

        public static final String CATEGORY = "MODEL_MEMBER_TYPE";
        private String code;

        MemberType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
