package com.hamsoo.bpm.model.dto.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FormCatalogDto {

    private Long id;
    @Size(max = 500, message = "error.formCatalog.faNameMaxLengthIsNotValid")
    @NotBlank(message = "error.formCatalog.faNameIsNotValid")
    private String faName;
    @Size(max = 500, message = "error.formCatalog.enNameMaxLengthIsNotValid")
    @NotBlank(message = "error.formCatalog.enNameIsNotValid")
    private String enName;
    @Size(max = 50, message = "error.formCatalog.formKeyMaxLengthIsNotValid")
    @NotBlank(message = "error.formCatalog.formKeyIsNotValid")
    private String formKey;


    //return property
    private Long organId;
    private FormVersionDto lastFormVersion;
    //search property
    private String searchText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFaName() {
        return faName;
    }

    public void setFaName(String faName) {
        this.faName = faName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public FormVersionDto getLastFormVersion() {
        return lastFormVersion;
    }

    public void setLastFormVersion(FormVersionDto lastFormVersion) {
        this.lastFormVersion = lastFormVersion;
    }
}
