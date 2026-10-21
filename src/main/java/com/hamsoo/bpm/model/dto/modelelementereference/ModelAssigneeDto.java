package com.hamsoo.bpm.model.dto.modelelementereference;

import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class ModelAssigneeDto {

    @NotNull(message = "error.modelTaskAssigneeDto.modelVersionIdIsNotValid")
    private Long modelVersionId;
    @NotBlank(message = "error.modelTaskAssigneeDto.elementIdIsNotValid")
    private String elementId;
    private String parentLaneId;
    private ModelVersionDto parentModelVersionDto;

    @NotNull(message = "error.modelTaskAssigneeDto.assigneeListNotValid")
    @Size(min = 1, message = "error.modelTaskAssigneeDto.assigneeListSizeNotValid")
    private List<AssigneeDto> assigneeDtos;


    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }


    public ModelVersionDto getParentModelVersionDto() {
        return parentModelVersionDto;
    }

    public void setParentModelVersionDto(ModelVersionDto parentModelVersionDto) {
        this.parentModelVersionDto = parentModelVersionDto;
    }

    public List<AssigneeDto> getAssigneeDtos() {
        return assigneeDtos;
    }

    public void setAssigneeDtos(List<AssigneeDto> assigneeDtos) {
        this.assigneeDtos = assigneeDtos;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getParentLaneId() {
        return parentLaneId;
    }

    public void setParentLaneId(String parentLaneId) {
        this.parentLaneId = parentLaneId;
    }
}
