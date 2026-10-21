package com.hamsoo.bpm.model.dto.kpiunit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class KpiUnitDto {

    private Long id;
    @Size(max = 100, message = "error.kpiUnit.unitCodeMaxLengthIsNotValid")
    @NotBlank(message = "error.kpiUnit.unitCodeIsNotValid")
    private String unitCode;
    @Size(max = 100, message = "error.kpiUnit.unitNameMaxLengthIsNotValid")
    @NotBlank(message = "error.kpiUnit.unitNameIsNotValid")
    private String unitName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
