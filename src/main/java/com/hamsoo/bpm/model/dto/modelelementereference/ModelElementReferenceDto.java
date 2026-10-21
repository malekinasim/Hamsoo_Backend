package com.hamsoo.bpm.model.dto.modelelementereference;

import com.hamsoo.bpm.model.dto.externalprocess.ExternalProcessDto;
import com.hamsoo.bpm.model.dto.modelcomment.ModelCommentDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ModelElementReferenceDto {

    private Long id;
    @NotNull(message = "error.modelElementReferenceDto.modelVersionIdIsNotValid")
    private Long modelVersionId;

    @NotNull(message = "error.modelElementReferenceDto.modelReferenceIdIsNotValid")
    private Long modelReferenceId;

    @NotBlank(message = "error.modelElementReferenceDto.elementIdIsNotValid")
    private String elementId;

    @NotBlank(message = "error.modelElementReferenceDto.referenceTypeCodeISNotValid")
    private String referenceTypeCode;

    private String referenceTypeTitle;

    private ModelVersionDto parentModelVersionDto;

    private ModelVersionDto referenceModelVersionDto;
    private ExternalProcessDto externalProcessDto;


    private ModelCommentDto modelCommentDto;


    //search property for InternalPrc
    private List<String> modelTypeCodeList;
    private Boolean filterHasReference = false;

    private List<String> modelReferenceTypeList;


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

    public Long getModelReferenceId() {
        return modelReferenceId;
    }

    public void setModelReferenceId(Long modelReferenceId) {
        this.modelReferenceId = modelReferenceId;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getReferenceTypeCode() {
        return referenceTypeCode;
    }

    public void setReferenceTypeCode(String referenceTypeCode) {
        this.referenceTypeCode = referenceTypeCode;
    }

    public String getReferenceTypeTitle() {
        return referenceTypeTitle;
    }

    public void setReferenceTypeTitle(String referenceTypeTitle) {
        this.referenceTypeTitle = referenceTypeTitle;
    }

    public ModelVersionDto getParentModelVersionDto() {
        return parentModelVersionDto;
    }

    public void setParentModelVersionDto(ModelVersionDto parentModelVersionDto) {
        this.parentModelVersionDto = parentModelVersionDto;
    }

    public ExternalProcessDto getExternalProcessDto() {
        return externalProcessDto;
    }

    public void setExternalProcessDto(ExternalProcessDto externalProcessDto) {
        this.externalProcessDto = externalProcessDto;
    }

    public ModelVersionDto getReferenceModelVersionDto() {
        return referenceModelVersionDto;
    }

    public void setReferenceModelVersionDto(ModelVersionDto referenceModelVersionDto) {
        this.referenceModelVersionDto = referenceModelVersionDto;
    }

    public List<String> getModelTypeCodeList() {
        return modelTypeCodeList;
    }

    public void setModelTypeCodeList(List<String> modelTypeCodeList) {
        this.modelTypeCodeList = modelTypeCodeList;
    }

    public Boolean getFilterHasReference() {
        return filterHasReference;
    }

    public void setFilterHasReference(Boolean filterHasReference) {
        this.filterHasReference = filterHasReference;
    }


    public ModelCommentDto getModelCommentDto() {
        return modelCommentDto;
    }

    public void setModelCommentDto(ModelCommentDto modelCommentDto) {
        this.modelCommentDto = modelCommentDto;
    }

    public List<String> getModelReferenceTypeList() {
        return modelReferenceTypeList;
    }

    public void setModelReferenceTypeList(List<String> modelReferenceTypeList) {
        this.modelReferenceTypeList = modelReferenceTypeList;
    }
}
