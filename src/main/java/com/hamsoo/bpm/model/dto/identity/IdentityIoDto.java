package com.hamsoo.bpm.model.dto.identity;

import com.hamsoo.bpm.model.dto.iocatalog.IoCatalogDto;
import com.hamsoo.bpm.model.dto.error.ErrorDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class IdentityIoDto {

    private Long id;
    @NotBlank(message = "error.identityIo.typeCodeIsNotValid")
    private String typeCode;
    private String typeTitle;
    @NotNull(message = "error.identityIo.ioCatalogIdIsNotValid")
    private Long ioCatalogId;
    @NotNull(message = "error.identityIo.modelVersionIdIsNotValid")
    private Long modelVersionId;

    //return property
    private IoCatalogDto ioCatalogDto;
    private Long supCustCount;
    private List<ErrorDto> errorDtoList;


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

    public Long getIoCatalogId() {
        return ioCatalogId;
    }

    public void setIoCatalogId(Long ioCatalogId) {
        this.ioCatalogId = ioCatalogId;
    }

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public IoCatalogDto getIoCatalogDto() {
        return ioCatalogDto;
    }

    public void setIoCatalogDto(IoCatalogDto ioCatalogDto) {
        this.ioCatalogDto = ioCatalogDto;
    }

    public Long getSupCustCount() {
        return supCustCount;
    }

    public void setSupCustCount(Long supCustCount) {
        this.supCustCount = supCustCount;
    }

    public List<ErrorDto> getErrorDtoList() {
        return errorDtoList;
    }

    public void setErrorDtoList(List<ErrorDto> errorDtoList) {
        this.errorDtoList = errorDtoList;
    }
}
