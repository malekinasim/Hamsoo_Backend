package com.hamsoo.bpm.model.dto.identity;

import com.hamsoo.bpm.model.dto.error.ErrorDto;
import com.hamsoo.bpm.model.dto.externalprocess.ExternalProcessDto;
import com.hamsoo.bpm.model.dto.modelversion.ModelVersionDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class IoSupCustDto {

    private Long id;
    @NotBlank(message = "error.ioSupCust.typeCodeIsNotValid")
    private String typeCode;
    private String typeTitle;
    private Long internalSupCustId;
    private Long externalSupCustId;
    @Size(max = 100, message = "error.ioSupCust.eventSupCustMaxLengthIsNotValid")
    private String eventSupCust;
    @NotNull(message = "error.ioSupCust.identityIoIdIsNotValid")
    private Long identityIoId;

    //return property
    private ModelVersionDto internalSupCust;
    private ExternalProcessDto externalSupCust;
    private List<ErrorDto> errorDtoList;

    //search property
    private List<String> modelTypeCodeList;
    private Boolean filterHasReference = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public Long getInternalSupCustId() {
        return internalSupCustId;
    }

    public void setInternalSupCustId(Long internalSupCustId) {
        this.internalSupCustId = internalSupCustId;
    }

    public Long getExternalSupCustId() {
        return externalSupCustId;
    }

    public void setExternalSupCustId(Long externalSupCustId) {
        this.externalSupCustId = externalSupCustId;
    }

    public String getEventSupCust() {
        return eventSupCust;
    }

    public void setEventSupCust(String eventSupCust) {
        this.eventSupCust = eventSupCust;
    }

    public Long getIdentityIoId() {
        return identityIoId;
    }

    public void setIdentityIoId(Long identityIoId) {
        this.identityIoId = identityIoId;
    }

    public ModelVersionDto getInternalSupCust() {
        return internalSupCust;
    }

    public void setInternalSupCust(ModelVersionDto internalSupCust) {
        this.internalSupCust = internalSupCust;
    }

    public ExternalProcessDto getExternalSupCust() {
        return externalSupCust;
    }

    public void setExternalSupCust(ExternalProcessDto externalSupCust) {
        this.externalSupCust = externalSupCust;
    }

    public List<ErrorDto> getErrorDtoList() {
        return errorDtoList;
    }

    public void setErrorDtoList(List<ErrorDto> errorDtoList) {
        this.errorDtoList = errorDtoList;
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
}
