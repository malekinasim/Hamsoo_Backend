package com.hamsoo.bpm.model.entity.iocatalog;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.iotype.IoType;
import com.hamsoo.bpm.model.entity.organ.Organ;

import javax.persistence.*;

@Entity
@Table(name = "bpm_io_catalogs", schema = "bpm", catalog = "BPM_DB")
public class IoCatalog extends AuditEntity<Long> {

    private Long id;
    private String name;
    private String status;
    private IoType ioType;
    private Organ organ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "io_id")
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
    public IoType getIoType() {
        return ioType;
    }

    public void setIoType(IoType ioType) {
        this.ioType = ioType;
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
