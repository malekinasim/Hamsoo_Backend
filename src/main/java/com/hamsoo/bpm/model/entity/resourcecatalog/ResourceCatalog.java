package com.hamsoo.bpm.model.entity.resourcecatalog;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.resourcetype.ResourceType;

import javax.persistence.*;

@Entity
@Table(name = "bpm_resource_catalogs", schema = "bpm", catalog = "BPM_DB")
public class ResourceCatalog extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String status;
    private ResourceType resourceType;
    private Organ organ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id")
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }
}
