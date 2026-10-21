package com.hamsoo.bpm.model.entity.saleplanfeature;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.feature.Feature;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;

import javax.persistence.*;

@Entity
@Table(name = "bpm_splan_featuers", schema = "bpm", catalog = "BPM_DB")
public class SalePlanFeature  extends AuditEntity<Long> {
    private Long id;

    private SalePlan salePlan;
    private Feature feature;


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "splan_featuer_id")
    @Override
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @ManyToOne
    @JoinColumn(name = "sale_plan_id", referencedColumnName = "sale_plan_id", nullable = false)
    public SalePlan getSalePlan() {
        return salePlan;
    }
    public void setSalePlan(SalePlan salePlan) {
        this.salePlan = salePlan;
    }

    @ManyToOne
    @JoinColumn(name = "feature_id", referencedColumnName = "feature_id", nullable = false)
    public Feature getFeature() {
        return feature;
    }
    public void setFeature(Feature feature) {
        this.feature = feature;
    }

}
