package com.hamsoo.bpm.model.entity.kpicatalog;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.kpiunit.KpiUnit;
import com.hamsoo.bpm.model.entity.organ.Organ;

import javax.persistence.*;

@Entity
@Table(name = "bpm_kpi_catalogs", schema = "bpm", catalog = "BPM_DB")
public class KpiCatalog extends AuditEntity<Long> {

    private Long id;
    private String name;
    private KpiUnit kpiUnit;
   // private String kpiAcceptMethod;
    private Organ organ;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kpi_id")
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
    @JoinColumn(name = "kpi_unit")
    public KpiUnit getKpiUnit() {
        return kpiUnit;
    }

    public void setKpiUnit(KpiUnit kpiUnit) {
        this.kpiUnit = kpiUnit;
    }

//    @Basic
//    @Column(name = "kpi_accept_method")
//    public String getKpiAcceptMethod() {
//        return kpiAcceptMethod;
//    }
//
//    public void setKpiAcceptMethod(String kpiAcceptMethod) {
//        this.kpiAcceptMethod = kpiAcceptMethod;
//    }


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

}
