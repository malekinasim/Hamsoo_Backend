package com.hamsoo.bpm.model.entity.modeldocument;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.files.Files;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;

import javax.persistence.*;

@Entity
@Table(name = "bpm_model_documents", schema = "bpm", catalog = "BPM_DB")
public class ModelDocument extends AuditEntity<Long> {

    private Long id;
    private Files file;
    private ModelVersion modelVersion;
    private String name;
    private String description;
    private String elementId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id")
    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(ModelVersion modelVersion) {
        this.modelVersion = modelVersion;
    }

    @Basic
    @Column(name = "name")
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


    @Basic
    @Column(name = "element_id")
    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }
}
