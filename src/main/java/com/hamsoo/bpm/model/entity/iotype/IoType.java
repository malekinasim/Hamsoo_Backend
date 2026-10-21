package com.hamsoo.bpm.model.entity.iotype;

import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "bpm_io_types", schema = "bpm", catalog = "BPM_DB")
public class IoType extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "io_type_id")
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

}
