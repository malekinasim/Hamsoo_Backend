package com.hamsoo.bpm.model.dto.kpicatalog;

import com.hamsoo.bpm.model.dto.kpiunit.KpiUnitDto;

import javax.validation.constraints.NotBlank;

public class KpiCatalogDto {

    private Long id;
    @NotBlank(message = "error.kpiCatalog.nameIsNotValid")
    private String name;
    private Long kpiUnitId;

    private Long organId;

    //return property
    private KpiUnitDto kpiUnitDto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getKpiUnitId() {
        return kpiUnitId;
    }

    public void setKpiUnitId(Long kpiUnitId) {
        this.kpiUnitId = kpiUnitId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public KpiUnitDto getKpiUnitDto() {
        return kpiUnitDto;
    }

    public void setKpiUnitDto(KpiUnitDto kpiUnitDto) {
        this.kpiUnitDto = kpiUnitDto;
    }

}
