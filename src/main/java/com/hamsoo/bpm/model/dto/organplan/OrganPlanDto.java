package com.hamsoo.bpm.model.dto.organplan;

import com.hamsoo.bpm.model.dto.saleplan.SalePlanDto;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class OrganPlanDto {
    private Long id;
    private SalePlanDto salePlanDto;
    private Long salePlanId;
    private Long organId;
    private Integer maxUserCount;
    private Integer usedUserCount;
    private Integer maxModelCount;
    private Integer usedModelCount;
    private BigInteger maxCapacity;
    private String humanReadableMaxCapacity;
    private String usedCapacity;
    private String remainCapacity;
    private BigInteger amountPaid;
    private Integer RemainingOrganPlanDay;
    //for return and search
    private LocalDateTime validFrom;
    private LocalDateTime validTo;


    public Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public Long getSalePlanId() {
        return salePlanId;
    }

    public void setSalePlanId(Long salePlanId) {
        this.salePlanId = salePlanId;
    }


    public Integer getMaxUserCount() {
        return maxUserCount;
    }

    public void setMaxUserCount(Integer maxUserCount) {
        this.maxUserCount = maxUserCount;
    }

    public Integer getMaxModelCount() {
        return maxModelCount;
    }

    public void setMaxModelCount(Integer maxModelCount) {
        this.maxModelCount = maxModelCount;
    }

    public BigInteger getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(BigInteger maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public BigInteger getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigInteger amountPaid) {
        this.amountPaid = amountPaid;
    }

    public SalePlanDto getSalePlanDto() {
        return salePlanDto;
    }

    public void setSalePlanDto(SalePlanDto salePlanDto) {
        this.salePlanDto = salePlanDto;
    }

    public Integer getUsedUserCount() {
        return usedUserCount;
    }

    public void setUsedUserCount(Integer usedUserCount) {
        this.usedUserCount = usedUserCount;
    }

    public Integer getUsedModelCount() {
        return usedModelCount;
    }

    public void setUsedModelCount(Integer usedModelCount) {
        this.usedModelCount = usedModelCount;
    }




    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Integer getRemainingOrganPlanDay() {
        return RemainingOrganPlanDay;
    }

    public void setRemainingOrganPlanDay(Integer remainingOrganPlanDay) {
        RemainingOrganPlanDay = remainingOrganPlanDay;
    }

    public String getHumanReadableMaxCapacity() {
        return humanReadableMaxCapacity;
    }

    public void setHumanReadableMaxCapacity(String humanReadableMaxCapacity) {
        this.humanReadableMaxCapacity = humanReadableMaxCapacity;
    }

    public String getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(String usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public String getRemainCapacity() {
        return remainCapacity;
    }

    public void setRemainCapacity(String remainCapacity) {
        this.remainCapacity = remainCapacity;
    }
}
