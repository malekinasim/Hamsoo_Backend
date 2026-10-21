package com.hamsoo.bpm.model.dto.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RuleDto {

    private Long id;
    @Size(max = 500, message = "error.rule.titleMaxLengthIsNotValid")
    @NotBlank(message = "error.rule.titleIsNotValid")
    private String title;
    @Size(max = 1000, message = "error.rule.descriptionMaxLengthIsNotValid")
    private String description;
    private Integer fromVersion;
    @NotNull(message = "error.rule.typeIdIsNotValid")
    private Long typeId;
    private Long formVersionId;

    //return property
    private FormLovCatalogDto type;

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

    public Integer getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(Integer fromVersion) {
        this.fromVersion = fromVersion;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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
}
