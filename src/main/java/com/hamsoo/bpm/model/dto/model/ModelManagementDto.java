package com.hamsoo.bpm.model.dto.model;

import com.hamsoo.bpm.model.dto.modelmemberrole.ModelMemberRoleDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;
import com.hamsoo.bpm.model.entity.modelversion.ModelVersion;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ModelManagementDto {

    @NotBlank(message = "error.modelManagement.nameIsNotValid")
    private String name;
    //    @NotBlank(message = "error.modelManagement.codeIsNotValid")
    private String code;
    private String description;
    private String mission;
    @NotBlank(message = "error.modelManagement.modelTypeCodeIsNotValid")
    private String modelTypeCode;
    private Long processScopeId;
    private Long processCategoryId;
    @NotNull(message = "error.modelManagement.asisTobeIsNotValid")
    private ModelVersion.AsisTobe asisTobe;
    private Long responsibleUnitId;

    //return property
    private ModelDto modelDto;
    private List<ModelVersionDto> modelVersionDtoList;
    private List<ModelMemberRoleDto> modelMemberDtoList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getModelTypeCode() {
        return modelTypeCode;
    }

    public void setModelTypeCode(String modelTypeCode) {
        this.modelTypeCode = modelTypeCode;
    }

    public Long getProcessScopeId() {
        return processScopeId;
    }

    public void setProcessScopeId(Long processScopeId) {
        this.processScopeId = processScopeId;
    }

    public Long getProcessCategoryId() {
        return processCategoryId;
    }

    public void setProcessCategoryId(Long processCategoryId) {
        this.processCategoryId = processCategoryId;
    }

    public ModelVersion.AsisTobe getAsisTobe() {
        return asisTobe;
    }

    public void setAsisTobe(ModelVersion.AsisTobe asisTobe) {
        this.asisTobe = asisTobe;
    }

    public Long getResponsibleUnitId() {
        return responsibleUnitId;
    }

    public void setResponsibleUnitId(Long responsibleUnitId) {
        this.responsibleUnitId = responsibleUnitId;
    }

    public ModelDto getModelDto() {
        return modelDto;
    }

    public void setModelDto(ModelDto modelDto) {
        this.modelDto = modelDto;
    }

    public List<ModelVersionDto> getModelVersionDtoList() {
        return modelVersionDtoList;
    }

    public void setModelVersionDtoList(List<ModelVersionDto> modelVersionDtoList) {
        this.modelVersionDtoList = modelVersionDtoList;
    }

    public List<ModelMemberRoleDto> getModelMemberDtoList() {
        return modelMemberDtoList;
    }

    public void setModelMemberDtoList(List<ModelMemberRoleDto> modelMemberDtoList) {
        this.modelMemberDtoList = modelMemberDtoList;
    }
}
