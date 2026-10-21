package com.hamsoo.bpm.model.dto.form;

import javax.validation.constraints.NotBlank;


public class FormLovCatalogDto {

    private Long id;
    @NotBlank(message = "error.formLovCatalog.nameIsNotValid")
    private String name;
    private String typeCode;
    private String typeTitle;

    //return property
    private Long organId;

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

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }
}
