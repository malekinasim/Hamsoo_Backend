package com.hamsoo.bpm.model.entity.externalprocess;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;

import javax.persistence.*;

@Entity
@Table(name = "bpm_external_process", schema = "bpm", catalog = "BPM_DB")
public class ExternalProcess extends AuditEntity<Long> {

    private Long id;
    private String name;
    private Organ organ;
    private String status;

    /// private ModelElementRefrence modelElementRefrence;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organ_id")
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "refrence_id")
//    public ModelElementRefrence getModelElementRefrence() {
//        return modelElementRefrence;
//    }
//
//    public void setModelElementRefrence(ModelElementRefrence modelElementRefrence) {
//        this.modelElementRefrence = modelElementRefrence;
//    }
}
