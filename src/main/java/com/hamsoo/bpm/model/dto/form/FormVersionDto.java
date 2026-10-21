package com.hamsoo.bpm.model.dto.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class FormVersionDto {

    private Long id;
    @Size(max = 1000, message = "error.formVersion.descriptionMaxLengthIsNotValid")
    private String description;
    @NotNull(message = "error.formVersion.publishStatusIdIsNotValid")
    private Long publishStatusId;
    private Long formCatalogId;

    //return property
    private FormLovCatalogDto formLovCatalogDto;
    private Integer formVersion;
    private Boolean lastVersion;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPublishStatusId() {
        return publishStatusId;
    }

    public void setPublishStatusId(Long publishStatusId) {
        this.publishStatusId = publishStatusId;
    }

    public Long getFormCatalogId() {
        return formCatalogId;
    }

    public void setFormCatalogId(Long formCatalogId) {
        this.formCatalogId = formCatalogId;
    }

    public FormLovCatalogDto getFormLovCatalogDto() {
        return formLovCatalogDto;
    }

    public void setFormLovCatalogDto(FormLovCatalogDto formLovCatalogDto) {
        this.formLovCatalogDto = formLovCatalogDto;
    }

    public Integer getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(Integer formVersion) {
        this.formVersion = formVersion;
    }

    public Boolean getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        this.lastVersion = lastVersion;
    }

    public LocalDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDateTime getToDate() {
        return toDate;
    }

    public void setToDate(LocalDateTime toDate) {
        this.toDate = toDate;
    }
}
