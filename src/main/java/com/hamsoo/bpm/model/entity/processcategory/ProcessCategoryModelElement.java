package com.hamsoo.bpm.model.entity.processcategory;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;

import javax.persistence.*;

@Entity
@Table(name = "bpm_process_categories_model_elements", schema = "bpm", catalog = "BPM_DB")
public class ProcessCategoryModelElement extends AuditEntity<Long> {

    private Long id;
    private String elementId;
    private ModelVersion modelVersion;
    private ProcessCategory processCategory;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "element_id", nullable = false, length = -1)
    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_category_id")
    public ProcessCategory getProcessCategory() {
        return processCategory;
    }

    public void setProcessCategory(ProcessCategory processCategory) {
        this.processCategory = processCategory;
    }
}
