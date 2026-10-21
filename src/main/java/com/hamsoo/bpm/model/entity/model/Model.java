package com.hamsoo.bpm.model.entity.model;

import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.unit.Unit;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "bpm_models", schema = "bpm", catalog = "BPM_DB")
public class Model extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String code;
    private CodeCategory modelType;
    private Set<ModelVersion> modelVersions;
    private Organ organ;
    private String status;
    private LocalDateTime modelLastModificationDate;
    private Unit responsibleUnit;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "code", nullable = false, length = -1)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_type", nullable = false)
    public CodeCategory getModelType() {
        return modelType;
    }

    public void setModelType(CodeCategory modelType) {
        this.modelType = modelType;
    }

    @OneToMany(mappedBy = "model")
    public Set<ModelVersion> getModelVersions() {
        return modelVersions;
    }

    public void setModelVersions(Set<ModelVersion> modelVersions) {
        this.modelVersions = modelVersions;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id")
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    @Basic
    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Formula("(SELECT MAX(_model_last_modification_date) FROM " +
            "    (SELECT distinct m.model_id as _model_id, " +
            "     GREATEST(f.modified_date,mv.modified_date,m.modified_date,ii.modified_date,sc.modified_date,ik.modified_date) " +
            "     as _model_last_modification_date " +
            "     FROM  bpm.bpm_model_versions mv " +
            "     left outer join bpm.bpm_files f on mv.bpmn = f.file_id " +
            "     inner join bpm.bpm_models m on m.model_id = mv.model_id " +
            "     left outer join bpm.bpm_identity_io ii on ii.model_version_id = mv.model_version_id " +
            "     left outer join bpm.bpm_identity_io_sup_custs sc on sc.identity_io_id = ii.identity_io_id " +
            "     left outer join bpm.bpm_identity_kpi ik on ik.model_version_id = mv.model_version_id " +
            "     where mv.last_version=true and m.model_id = model_id) as _temp_table " +
            "     GROUP BY _model_id) ")
    public LocalDateTime getModelLastModificationDate() {
        return modelLastModificationDate;
    }


    public void setModelLastModificationDate(LocalDateTime modelLastModificationDate) {
        this.modelLastModificationDate = modelLastModificationDate;
    }

//    @Formula("(SELECT   max(apprv.created_date)  as _model_assign_date " +
//            "            FROM  bpm.bpm_model_versions mv  " +
//            "            inner join bpm.bpm_models m on m.model_id = mv.model_id  " +
//            "            inner join bpm.bpm_code_category statusc on statusc.code_category_id=mv.status_id " +
//            "            inner join bpm.bpm_model_members mem on mv.model_version_id=mem.model_id " +
//            "            inner join bpm.bpm_model_roles rol on rol.model_role_id=mem.role_type_id " +
//            "            inner join bpm.bpm_model_approvers apprv on apprv.model_member_id=mem.model_member_id " +
//            "            where mv.last_version=true  " +
//            "            and statusc.category='MODEL_VERSION_STATUS' " +
//            "            and statusc.code='FOR_APPROVE' " +
//            "            and rol.code='MODEL_APPROVER' " +
//            "             and m.model_id = model_id " +
//            "           group by m.model_id)")
//    public LocalDateTime getModelAssignDate() {
//        return modelAssignDate;
//    }
//
//    public void setModelAssignDate(LocalDateTime modelAssignDate) {
//        this.modelAssignDate = modelAssignDate;
//    }
//
//   @Formula("(select STRING_AGG(_temp_table1._model_assign_status ,',')  as _model_assign_status " +
//           " from  (SELECT  distinct  lvlst.fa_name  as _model_assign_status ,m.model_id " +
//           "            FROM  bpm.bpm_model_versions mv  " +
//           "            inner join bpm.bpm_models m on m.model_id = mv.model_id  " +
//           "            inner join bpm.bpm_code_category statusc on statusc.code_category_id=mv.status_id " +
//           "            inner join bpm.bpm_model_members mem on mv.model_version_id=mem.model_id " +
//           "            inner join bpm.bpm_model_roles rol on rol.model_role_id=mem.role_type_id " +
//           "            inner join bpm.bpm_model_approvers apprv on apprv.model_member_id=mem.model_member_id " +
//           "            inner join bpm.bpm_approver_logs logs on logs.model_approver_id=apprv.approver_id " +
//           "             inner join bpm.bpm_code_category logsst on logsst.code_category_id=logs.state " +
//           "                inner join bpm.bpm_code_category lvlst on lvlst.code_category_id=apprv.approve_level " +
//           "            where mv.last_version=true  " +
//           "            and statusc.code='FOR_APPROVE' " +
//           "            and rol.code='MODEL_APPROVER' " +
//           "            and logs.status='ACTIVE' " +
//           "            and apprv.status='ACTIVE' " +
//           "            and logsst.code in  ('FOR_YOUR_INFORMATION','FOR_APPROVE') " +
//           "            and m.model_id = model_id " +
//           "          ) _temp_table1 " +
//           "           group by _temp_table1.model_id)")
//    public String getModelAssignStatus() {
//        return modelAssignStatus;
//    }
//
//    public void setModelAssignStatus(String modelAssignStatus) {
//        this.modelAssignStatus = modelAssignStatus;
//    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_unit_id")
    public Unit getResponsibleUnit() {
        return responsibleUnit;
    }

    public void setResponsibleUnit(Unit responsibleUnit) {
        this.responsibleUnit = responsibleUnit;
    }

    public enum ModelType {
        ENTERPRISE_MAP("EM"),
        PROCESS_AREA("BG"),
        BUSINESS_PROCESS("BP"),
        VALUE_CHAIN("VC");

        public static final String CATEGORY = "MODEL_TYPE";

        private String code;

        ModelType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
