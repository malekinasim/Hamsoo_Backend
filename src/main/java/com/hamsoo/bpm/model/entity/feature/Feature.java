package com.hamsoo.bpm.model.entity.feature;

import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_features", schema = "bpm", catalog = "BPM_DB")
public class Feature extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String status= Status.ACTIVE.name();
    private LocalDateTime validFrom;
    private LocalDateTime validTo;


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "feature_id")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Basic
    @Column(name = "name",nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Basic
    @Column(name = "status",nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @Basic
    @Column(name = "valid_from",nullable = false)
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    @Basic
    @Column(name = "valid_to",nullable = false)
    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
}
