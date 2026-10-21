package com.hamsoo.bpm.model.entity.saleplan;

import com.hamsoo.bpm.model.enums.Status;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_sale_plans", schema = "bpm", catalog = "BPM_DB")
public class SalePlan extends AuditEntity<Long> {
    private Long id;
    private String description;
    private String name;
    private BigInteger price;
    private String status= Status.ACTIVE.name();
    private String planVersion;
    private String saleServiceType;
    private String code;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer maxUserCount;
    private Integer maxModelCount;
    private Integer planDuration;
    private BigInteger maxCapacity;



    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "sale_plan_id")
    @Override
    public Long getId() {return id;}
    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "description",nullable = false)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    @Column(name = "price",nullable = false)
    public BigInteger getPrice() {
        return price;
    }
    public void setPrice(BigInteger monthlyAmount) {
        this.price = monthlyAmount;
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
    @Column(name = "plan_version",nullable = false ,precision = 10)
    public String getPlanVersion() {
        return planVersion;
    }
    public void setPlanVersion(String code) {
        this.planVersion = code;
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


    @Basic
    @Column(name = "code",nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Basic
    @Column(name = "service_type",nullable = false)
    public String getSaleServiceType() {
        return saleServiceType;
    }

    public void setSaleServiceType(String saleServiceType) {
        this.saleServiceType = saleServiceType;
    }
    public enum ServicesType{
         SAAS,SELF_HOST,FREE,EXPIRED;
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

    public void setMaxCapacity(BigInteger maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Basic
    @Column(name = "plan_duration",nullable = false)
    public Integer getPlanDuration() {
        return planDuration;
    }
    public void setPlanDuration(Integer planDuration) {
        this.planDuration = planDuration;
    }
}
