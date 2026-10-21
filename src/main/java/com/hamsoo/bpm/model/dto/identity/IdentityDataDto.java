package com.hamsoo.bpm.model.dto.identity;

import com.hamsoo.bpm.model.dto.modelmember.ModelMemberDto;
import com.hamsoo.bpm.model.dto.processcategory.ProcessCategoryDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class IdentityDataDto {

    private String mission;
    private Long processCategoryId;
    private String scope;
    private String modelName;
    @NotBlank(message = "error.identity.modelCodeIsNotValid")
    private String modelCode;
    private List<ModelMemberDto> Owners;


    //return property
    private ProcessCategoryDto processCategoryDto;

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public Long getProcessCategoryId() {
        return processCategoryId;
    }

    public void setProcessCategoryId(Long processCategoryId) {
        this.processCategoryId = processCategoryId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public List<ModelMemberDto> getOwners() {
        return Owners;
    }

    public void setOwners(List<ModelMemberDto> owners) {
        Owners = owners;
    }

    public ProcessCategoryDto getProcessCategoryDto() {
        return processCategoryDto;
    }

    public void setProcessCategoryDto(ProcessCategoryDto processCategoryDto) {
        this.processCategoryDto = processCategoryDto;
    }
}
