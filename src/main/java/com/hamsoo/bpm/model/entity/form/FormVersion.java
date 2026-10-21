package com.hamsoo.bpm.model.entity.form;

import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bpm_form_versions", schema = "bpm", catalog = "BPM_DB")
public class FormVersion extends AuditEntity<Long> {

    private Long id;
    private String description;
    private Integer formVersion;
    private FormLovCatalog publishStatus;
    private Boolean lastVersion;
    private String status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private FormCatalog formCatalog;
    private Set<Rule> rules;
    private Set<Function> functions;
    private Set<FormEntity> formEntities;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_version_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @Column(name = "form_version")
    public Integer getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(Integer formVersion) {
        this.formVersion = formVersion;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publish_status_form_lov_catalog_id")
    public FormLovCatalog getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(FormLovCatalog publishStatus) {
        this.publishStatus = publishStatus;
    }

    @Basic
    @Column(name = "last_version")
    public Boolean getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        this.lastVersion = lastVersion;
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
    @Column(name = "from_date")
    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    @Basic
    @Column(name = "to_date")
    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_catalog_id")
    public FormCatalog getFormCatalog() {
        return formCatalog;
    }

    public void setFormCatalog(FormCatalog formCatalog) {
        this.formCatalog = formCatalog;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bpm_form_version_rules", schema = "bpm", catalog = "BPM_DB",
            joinColumns = {@JoinColumn(name = "form_version_id")},
            inverseJoinColumns = {@JoinColumn(name = "rule_id")})
    public Set<Rule> getRules() {
        return rules;
    }

    public void setRules(Set<Rule> rules) {
        this.rules = rules;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bpm_form_version_functions", schema = "bpm", catalog = "BPM_DB",
            joinColumns = {@JoinColumn(name = "form_version_id")},
            inverseJoinColumns = {@JoinColumn(name = "function_id")})
    public Set<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<Function> functions) {
        this.functions = functions;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bpm_form_version_entities", schema = "bpm", catalog = "BPM_DB",
            joinColumns = {@JoinColumn(name = "form_version_id")},
            inverseJoinColumns = {@JoinColumn(name = "entity_id")})
    public Set<FormEntity> getFormEntities() {
        return formEntities;
    }

    public void setFormEntities(Set<FormEntity> formEntities) {
        this.formEntities = formEntities;
    }

    public void addRule(Rule rule) {
        if (this.rules == null) {
            this.rules = new HashSet<>();
        }
        this.rules.add(rule);
    }

    public void addFunction(Function function) {
        if (this.functions == null) {
            this.functions = new HashSet<>();
        }
        this.functions.add(function);
    }

    public void addFormEntity(FormEntity formEntity) {
        if (this.formEntities == null) {
            this.formEntities = new HashSet<>();
        }
        this.formEntities.add(formEntity);
    }
}
