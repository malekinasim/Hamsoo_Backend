package com.hamsoo.bpm.model.entity.modelelementreference;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.codecategory.CodeCategory;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;

import javax.persistence.*;

@Entity
@Table(name = "bpm_model_element_references", schema = "bpm", catalog = "BPM_DB")
public class ModelElementRefrence extends AuditEntity<Long> {
    private Long id;
    private ModelVersion modelVersion;
    private Long referenceId;
    private String elementId;
    private CodeCategory referenceType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_element_reference_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_model_version_id", nullable = false)
    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }

    @Basic
    @Column(name = "reference_id", nullable = false)
    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long modelRefrenceId) {
        this.referenceId = modelRefrenceId;
    }

    @Basic
    @Column(name = "element_id", nullable = false)
    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_type")
    public CodeCategory getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(CodeCategory referenceType) {
        this.referenceType = referenceType;
    }

    public enum ReferenceType {

        INTERNAL("INTERNAL"),
        EXTERNAL("EXTERNAL"),
        COMMENT("COMMENT"),
        TASK_ASS_POST("TASK_ASS_POST"),
        TASK_ASS_JOB("TASK_ASS_JOB"),
        TASK_ASS_UNIT("TASK_ASS_UNIT"),
        TASK_ASS_USER("TASK_ASS_USER"),
        LANE_ASS_POST("LANE_ASS_POST"),
        LANE_ASS_JOB("LANE_ASS_JOB"),
        LANE_ASS_UNIT("LANE_ASS_UNIT"),
        LANE_ASS_USER("LANE_ASS_USER");


        public static final String CATEGORY = "MODEL_ELEMENT_REFERENCE_TYPE";
        private String code;

        ReferenceType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

    }

}
