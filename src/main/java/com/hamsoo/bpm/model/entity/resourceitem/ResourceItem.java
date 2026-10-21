package com.hamsoo.bpm.model.entity.resourceitem;


import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.feature.Feature;

import javax.persistence.*;

@Entity
@Table(name = "bpm_resource_items", schema = "bpm", catalog = "BPM_DB")
public class ResourceItem extends AuditEntity<Long> {
    private Long id;
    private String name;
    private String description;
    private String status = Status.ACTIVE.name();
    private Feature feature;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "feature_id", referencedColumnName = "feature_id")
    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public enum ResouceItemType {
        ASIS_TOBE_VERSION_MNG,
        MODEL_EDIT,
        MODEL_COMMENT_MNG,
        EM_MODEL_LANE_CATEGORY_MNG,
        MODEL_TREE_MNG,
        SALE_PLAN_MNG,
        MODEL_REFERENCE_MNG,
        MODEL_REFERENCE_APPROVER,
        MODEL_REFERENCE_DEFER,
        MODEL_REFERENCE_ACKNOWLEDGE,
        REFERENCE_MODELS_LIST,
        MODEL_TASK_ASSIGN_MNG,
        MODEL_IMAGE_FILE,
        MODEL_XML_FILE,
        MODEL_INFO,
        MODEL_CARD_INFO,
        PROCESS_GENERAL_MNG,
        MODEL_RESOURCE_EDIT,
        MODEL_RENAME,
        MODEL_REMOVE,
        MODEL_SIPOC_REPORT,
        MODEL_CREATE,
        MODEL_DOCUMENT_EDIT,
        MODEL_KPI_EDIT,
        MODEL_INPUT_OUTPUT_EDIT,
        MODEL_CONS_SUP_EDIT,
        PROCESS_TREE,
        WORKSPACE_MNG,
        EXTERNAL_PROCESS_MNG,
        INPUT_OUTPUT_MNG,
        IO_TYPE_MNG,
        KPI_MNG,
        KPI_TREND_TYPE_LIST,
        KPI_MONITOR_TYPE_LIST,
        KPI_DURATION_TYPE_LIST,
        ORGAN_MEMBER_MNG,
        MODEL_MEMBERS_MANAGE,
        MODEL_MEMBERS_LIST,
        PROCESS_CATEGORY_MNG,
        RESOURCE_MNG,
        RESOURCE_TYPE_MNG,
        UNIT_MNG,
        ORGANIZATIONAL_CHART_MNG,
        ORGANIZATIONAL_CHART_EDIT,
        ORGANIZATIONAL_CHART_TREE,
        ORGANIZATIONAL_CHART_POST_MNG,
        ORGANIZATIONAL_CHART_POST_MEMBER_MNG,
        ORGANIZATIONAL_CHART_UNIT_MNG,
        ORGANIZATIONAL_CHART_PERSON_MNG,
        ORGANIZATIONAL_CHART_JOB_MNG,
        MODEL_VALIDATION_REPORT,
        FINANCIAL_PANEL,
        MILESTONES_MNG,
        MODEL_DECISION_REPORT,
        MODEL_Risk_ADD,
        MODEL_Risk_DELETE,
        MODEL_Risk_EDIT,
        MODEL_Complication_ADD,
        MODEL_Complication_DELETE,
        MODEL_Complication_EDIT;
    }
}
