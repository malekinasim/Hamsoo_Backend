package com.hamsoo.bpm.model.entity.form;

import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_functions", schema = "bpm", catalog = "BPM_DB")
public class Function extends AuditEntity<Long> {

    private Long id;
    private String title;
    private String description;
    private FormLovCatalog type;
    private Integer fromVersion;
    private FormLovCatalog usageStatus;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "function_id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_form_lov_catalog_id")
    public FormLovCatalog getType() {
        return type;
    }

    public void setType(FormLovCatalog type) {
        this.type = type;
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
    @JoinColumn(name = "usage_status_form_lov_catalog_id")
    public FormLovCatalog getUsageStatus() {
        return usageStatus;
    }

    public void setUsageStatus(FormLovCatalog usageStatus) {
        this.usageStatus = usageStatus;
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
