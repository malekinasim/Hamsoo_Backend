package com.hamsoo.bpm.model.entity.identity;

import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;
import com.hamsoo.bpm.model.entity.resourcecatalog.ResourceCatalog;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_identity_resources", schema = "bpm")
public class IdentityResource extends AuditEntity<Long> {

    private Long id;
    private ModelVersion modelVersion;
    private String status;
    private ResourceCatalog resourceCatalog;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identity_resource_id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_catalog_id")
    public ResourceCatalog getResourceCatalog() {
        return resourceCatalog;
    }

    public void setResourceCatalog(ResourceCatalog resourceCatalog) {
        this.resourceCatalog = resourceCatalog;
    }
}
