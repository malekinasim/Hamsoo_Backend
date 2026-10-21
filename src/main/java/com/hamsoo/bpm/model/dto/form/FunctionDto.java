package com.hamsoo.bpm.model.dto.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FunctionDto {

    private Long id;
    @Size(max = 500, message = "error.function.titleMaxLengthIsNotValid")
    @NotBlank(message = "error.function.titleIsNotValid")
    private String title;
    @Size(max = 1000, message = "error.function.descriptionMaxLengthIsNotValid")
    private String description;
    @NotNull(message = "error.function.typeIdIsNotValid")
    private Long typeId;
    private Integer fromVersion;
    private Long usageStatusId;
    private Long formVersionId;

    //return property
    private FormLovCatalogDto type;
    private FormLovCatalogDto usageStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Integer getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(Integer fromVersion) {
        this.fromVersion = fromVersion;
    }

    public Long getUsageStatusId() {
        return usageStatusId;
    }

    public void setUsageStatusId(Long usageStatusId) {
        this.usageStatusId = usageStatusId;
    }

    public Long getFormVersionId() {
        return formVersionId;
    }

    public void setFormVersionId(Long formVersionId) {
        this.formVersionId = formVersionId;
    }

    public FormLovCatalogDto getType() {
        return type;
    }

    public void setType(FormLovCatalogDto type) {
        this.type = type;
    }

    public FormLovCatalogDto getUsageStatus() {
        return usageStatus;
    }

    public void setUsageStatus(FormLovCatalogDto usageStatus) {
        this.usageStatus = usageStatus;
    }
}
