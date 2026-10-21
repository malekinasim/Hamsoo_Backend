package com.hamsoo.bpm.model.dto.identity;

import com.hamsoo.bpm.model.dto.kpicatalog.KpiCatalogDto;

import javax.validation.constraints.NotNull;

public class IdentityKpiDto {

    private Long id;
    @NotNull(message = "error.identityKpi.modelVersionIdNotValid")
    private Long modelVersionId;
    @NotNull(message = "error.identityKpi.kpiCatalogIdNotValid")
    private Long kpiCatalogId;

    //return property
    private KpiCatalogDto kpiCatalogDto;


    private String kpiEvlDuration;
    private String kpiEvlDurationName;
    private String kpiOptimalTrend;
    private String kpiOptimalTrendName;
    private String kpiMonitorType;
    private String kpiMonitorTypeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public Long getKpiCatalogId() {
        return kpiCatalogId;
    }

    public void setKpiCatalogId(Long kpiCatalogId) {
        this.kpiCatalogId = kpiCatalogId;
    }

    public KpiCatalogDto getKpiCatalogDto() {
        return kpiCatalogDto;
    }

    public void setKpiCatalogDto(KpiCatalogDto kpiCatalogDto) {
        this.kpiCatalogDto = kpiCatalogDto;
    }


    public String getKpiEvlDuration() {
        return kpiEvlDuration;
    }

    public void setKpiEvlDuration(String kpiEvlDuration) {
        this.kpiEvlDuration = kpiEvlDuration;
    }


    public String getKpiOptimalTrend() {
        return kpiOptimalTrend;
    }

    public void setKpiOptimalTrend(String kpiOptimalTrend) {
        this.kpiOptimalTrend = kpiOptimalTrend;
    }

    public String getKpiMonitorType() {
        return kpiMonitorType;
    }

    public void setKpiMonitorType(String kpiMonitorType) {
        this.kpiMonitorType = kpiMonitorType;
    }

    public String getKpiEvlDurationName() {
        return kpiEvlDurationName;
    }

    public void setKpiEvlDurationName(String kpiEvlDurationName) {
        this.kpiEvlDurationName = kpiEvlDurationName;
    }

    public String getKpiOptimalTrendName() {
        return kpiOptimalTrendName;
    }

    public void setKpiOptimalTrendName(String kpiOptimalTrendName) {
        this.kpiOptimalTrendName = kpiOptimalTrendName;
    }

    public String getKpiMonitorTypeName() {
        return kpiMonitorTypeName;
    }

    public void setKpiMonitorTypeName(String kpiMonitorTypeName) {
        this.kpiMonitorTypeName = kpiMonitorTypeName;
    }


}
