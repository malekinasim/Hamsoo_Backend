package com.hamsoo.bpm.model.entity.modelversion;

import com.hamsoo.bpm.model.dto.identity.IdentityDataSchema;
import com.hamsoo.bpm.model.dto.identity.ResourceSchema;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.model.entity.model.Model;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
@Entity
@Table(name = "bpm_model_versions", schema = "bpm", catalog = "BPM_DB")
public class ModelVersion extends AuditEntity<Long> {

    private Long id;
    private Integer versionNum;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private CodeCategory asisTobe;
    private CodeCategory status;
    private Model model;
    private Boolean lastVersion;
    private Files xml;
    private Files image;
    private List<ResourceSchema> resourceSchemaList;
    private IdentityDataSchema identityDataSchema;
    private ModelVersion parent;
    private String milestoneName;
    private Files workFlowFile;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_version_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "version_num", nullable = false)
    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }

    @Basic
    @Column(name = "valid_from", nullable = false)
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    @Basic
    @Column(name = "valid_to")
    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asis_tobe_id", nullable = false)
    public CodeCategory getAsisTobe() {
        return asisTobe;
    }

    public void setAsisTobe(CodeCategory asisTobe) {
        this.asisTobe = asisTobe;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    public CodeCategory getStatus() {
        return status;
    }

    public void setStatus(CodeCategory status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", referencedColumnName = "model_id", nullable = false)
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Basic
    @Column(name = "last_version")
    public Boolean getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        this.lastVersion = lastVersion;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bpmn")
    public Files getXml() {
        return xml;
    }

    public void setXml(Files xml) {
        this.xml = xml;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bpmn_img")
    public Files getImage() {
        return image;
    }

    public void setImage(Files image) {
        this.image = image;
    }

    @Basic
    @Column(name = "resource_json", columnDefinition = "jsonb")
    @Type(type = "json")
    public List<ResourceSchema> getResourceSchemaList() {
        return resourceSchemaList;
    }

    public void setResourceSchemaList(List<ResourceSchema> resourceSchemaList) {
        this.resourceSchemaList = resourceSchemaList;
    }

    @Basic
    @Column(name = "identity_data_json", columnDefinition = "jsonb")
    @Type(type = "json")
    public IdentityDataSchema getIdentityDataSchema() {
        return identityDataSchema;
    }

    public void setIdentityDataSchema(IdentityDataSchema identityDataSchema) {
        this.identityDataSchema = identityDataSchema;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    public ModelVersion getParent() {
        return parent;
    }

    public void setParent(ModelVersion parent) {
        this.parent = parent;
    }

    @Basic
    @Column(name = "milestone_name")
    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_doc_file_id")
    public Files getWorkFlowFile() {
        return workFlowFile;
    }

    public void setWorkFlowFile(Files workFlowFile) {
        this.workFlowFile = workFlowFile;
    }


    public enum AsisTobe {
        AS_IS("AS_IS"),
        TO_BE("TO_BE");

        public static final String CATEGORY = "ASIS_TOBE";
        private String code;

        AsisTobe(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum Status {
        DRAFT("DRAFT"),
        FOR_APPROVE("FOR_APPROVE"),
        DEFFER("DEFFER"),
        APPROVE("APPROVE");

        public static final String CATEGORY = "MODEL_VERSION_STATUS";
        private String code;

        Status(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }


}
