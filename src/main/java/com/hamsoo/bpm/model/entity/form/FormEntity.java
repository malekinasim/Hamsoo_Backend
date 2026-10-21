package com.hamsoo.bpm.model.entity.form;

import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_entities", schema = "bpm", catalog = "BPM_DB")
public class FormEntity extends AuditEntity<Long> {

    private Long id;
    private String title;
    private String description;
    private String fromEntity;
    private String entityConditions;
    private Integer fieldCount;
    private String fieldTitle;
    private Integer fromVersion;
    private FormLovCatalog type;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    @Column(name = "from_entity")
    public String getFromEntity() {
        return fromEntity;
    }

    public void setFromEntity(String fromEntity) {
        this.fromEntity = fromEntity;
    }

    @Basic
    @Column(name = "entity_conditions")
    public String getEntityConditions() {
        return entityConditions;
    }

    public void setEntityConditions(String entityConditions) {
        this.entityConditions = entityConditions;
    }

    @Basic
    @Column(name = "feild_count")
    public Integer getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(Integer fieldCount) {
        this.fieldCount = fieldCount;
    }

    @Basic
    @Column(name = "feild_title")
    public String getFieldTitle() {
        return fieldTitle;
    }

    public void setFieldTitle(String fieldTitle) {
        this.fieldTitle = fieldTitle;
    }

    @Basic
    @Column(name = "from_version")
    public Integer getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(Integer fromVersion) {
        this.fromVersion = fromVersion;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_form_lov_catalog_id")
    public FormLovCatalog getType() {
        return type;
    }

    public void setType(FormLovCatalog type) {
        this.type = type;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
