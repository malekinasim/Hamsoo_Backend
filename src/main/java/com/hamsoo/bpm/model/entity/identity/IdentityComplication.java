package com.hamsoo.bpm.model.entity.identity;

import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_identity_complications", schema = "bpm")
public class IdentityComplication  extends AuditEntity<Long> {


    private Long id;
    private ModelVersion modelVersion;
    private String status;
    private String name;
    private String idea;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identity_complication_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    @Column(name = "idea")
    public String getIdea() {
        return idea;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

}
