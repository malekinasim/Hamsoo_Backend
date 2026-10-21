package com.hamsoo.bpm.model.entity.processcategory;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;

import javax.persistence.*;

@Entity
@Table(name = "bpm_process_categories", schema = "bpm", catalog = "BPM_DB")
public class ProcessCategory extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String description;
    private String status;
    private Organ organ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_category_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = false, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = -1)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "status", nullable = false, length = -1)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
