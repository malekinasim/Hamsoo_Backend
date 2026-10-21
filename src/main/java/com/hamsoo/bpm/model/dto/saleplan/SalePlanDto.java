package com.hamsoo.bpm.model.dto.saleplan;

import com.hamsoo.bpm.model.dto.saleplanfeature.SalePlanFeatureDto;

import java.math.BigInteger;
import java.util.List;

public class SalePlanDto {
    private java.lang.Long id;
    private String description;
    //search and return
    private String name;
    private String code;
    private BigInteger price;
    private List<SalePlanFeatureDto> featureList;
    private String planVersion;
    private String ServiceType;
    private BigInteger maxCapacity;
    private Integer maxModelCount;
    private Integer maxUserCount;
    private Integer planDuration;
    //search
    private List<String> ServiceTypes;


    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public List<SalePlanFeatureDto> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<SalePlanFeatureDto> featureList) {
        this.featureList = featureList;
    }
    public String getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion(String planVersion) {
        this.planVersion = planVersion;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public void setServiceType(String serviceType) {
        ServiceType = serviceType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getServiceTypes() {
        return ServiceTypes;
    }

    public void setServiceTypes(List<String> serviceTypes) {
        ServiceTypes = serviceTypes;
    }

    public BigInteger getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(BigInteger maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getMaxModelCount() {
        return maxModelCount;
    }

    public void setMaxModelCount(Integer maxModelCount) {
        this.maxModelCount = maxModelCount;
    }

    public Integer getMaxUserCount() {
        return maxUserCount;
    }

    public void setMaxUserCount(Integer maxUserCount) {
        this.maxUserCount = maxUserCount;
    }

    public Integer getPlanDuration() {
        return planDuration;
    }

    public void setPlanDuration(Integer planDuration) {
        this.planDuration = planDuration;
    }
}
