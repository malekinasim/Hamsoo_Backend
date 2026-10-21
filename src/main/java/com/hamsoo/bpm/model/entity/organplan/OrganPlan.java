package com.hamsoo.bpm.model.entity.organplan;

import com.hamsoo.bpm.model.entity.base.AuditEntity;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.model.entity.saleplan.SalePlan;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_organ_plans", schema = "bpm", catalog = "BPM_DB")
public class OrganPlan extends AuditEntity<Long> {
    private Long id;
    private Organ organ;
    private SalePlan salePlan;
    private Integer maxUserCount;
    private Integer maxModelCount;
    private BigInteger maxCapacity;
    private BigInteger amountPaid;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "organ_plan_id")
    @Override
    public Long getId() {return id;}
    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "max_user_count",nullable = false)
    public Integer getMaxUserCount() {
        return maxUserCount;
    }

    public void setMaxUserCount(Integer maxUserCount) {
        this.maxUserCount = maxUserCount;
    }
    @Basic
    @Column(name = "max_model_count",nullable = false)
    public Integer getMaxModelCount() {
        return maxModelCount;
    }

    public void setMaxModelCount(Integer maxModelCount) {
        this.maxModelCount = maxModelCount;
    }
    @Basic
    @Column(name = "max_capacity",nullable = false)
    public BigInteger getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(BigInteger maxCapacityCount) {
        this.maxCapacity = maxCapacityCount;
    }
    @Basic
    @Column(name = "from_date",nullable = false)
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }
    @Basic
    @Column(name = "to_date")
    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }


    @Basic
    @Column(name = "amount_paid")
    public BigInteger getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigInteger amountPaid) {
        this.amountPaid = amountPaid;
    }

    @ManyToOne
    @JoinColumn(name = "organ_id", referencedColumnName = "organ_id", nullable = false)
    public Organ getOrgan() {
        return organ;
    }

    public void setOrgan(Organ organ) {
        this.organ = organ;
    }
    @ManyToOne
    @JoinColumn(name = "sale_plan_id", nullable = false)
    public SalePlan getSalePlan() {
        return salePlan;
    }

    public void setSalePlan(SalePlan salePlan) {
        this.salePlan = salePlan;
    }

}
