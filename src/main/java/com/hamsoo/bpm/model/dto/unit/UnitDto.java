package com.hamsoo.bpm.model.dto.unit;

import com.hamsoo.bpm.model.dto.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class UnitDto {

    private Long id;
    @NotBlank(message = "error.unit.nameIsNotValid")
    @Size(max = 500, message = "error.unit.nameSizeNotValid")
    private String name;
    private LocalDateTime validTo;
    private LocalDateTime validFrom;
    private String sourceUnitId;
    @NotBlank(message = "error.unit.codeIsNotValid")
    @Size(max = 10, message = "error.unit.codeSizeNotValid")
    private String code;
    @NotNull(message = "error.unit.chartIdNotValid")
    private Long chartId;
    private Long parentId;
    private List<UserDto> userDtoList;

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

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public String getSourceUnitId() {
        return sourceUnitId;
    }

    public void setSourceUnitId(String sourceUnitId) {
        this.sourceUnitId = sourceUnitId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<UserDto> getUserDtoList() {
        return userDtoList;
    }

    public void setUserDtoList(List<UserDto> userDtoList) {
        this.userDtoList = userDtoList;
    }
}
