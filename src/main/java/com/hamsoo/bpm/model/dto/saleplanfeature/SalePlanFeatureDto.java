package com.hamsoo.bpm.model.dto.saleplanfeature;

import com.hamsoo.bpm.model.dto.feature.FeatureDto;

import java.util.List;

public class SalePlanFeatureDto {
    private Long id;
    //private BigInteger count;
    //for create and return and search
    private Long salePlanId;
    private Long featureId;
    private FeatureDto featureDto;
    //for return and search
    private String featureName;
    private String salePlanName;
    //for search
    private List<String> featureCalcTypeList;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSalePlanId() {
        return salePlanId;
    }

    public void setSalePlanId(Long salePlanId) {
        this.salePlanId = salePlanId;
    }

    public Long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Long featureId) {
        this.featureId = featureId;
    }

    public String getFeatureName() {
        return featureName;
    }


    public String getSalePlanName() {
        return salePlanName;
    }

    public void setSalePlanName(String salePlanName) {
        this.salePlanName = salePlanName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public FeatureDto getFeatureDto() {
        return featureDto;
    }

    public void setFeatureDto(FeatureDto featureDto) {
        this.featureDto = featureDto;
    }

    public List<String> getFeatureCalcTypeList() {
        return featureCalcTypeList;
    }

    public void setFeatureCalcTypeList(List<String> featureCalcTypeList) {
        this.featureCalcTypeList = featureCalcTypeList;
    }
}
