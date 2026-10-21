package com.hamsoo.bpm.model.dto.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FormEntityDto {

    private Long id;
    @Size(max = 500, message = "error.formEntity.titleMaxLengthIsNotValid")
    @NotBlank(message = "error.formEntity.titleIsNotValid")
    private String title;
    @Size(max = 1000, message = "error.formEntity.descriptionMaxLengthIsNotValid")
    private String description;
    @Size(max = 500, message = "error.formEntity.fromEntityMaxLengthIsNotValid")
    private String fromEntity;
    @Size(max = 1000, message = "error.formEntity.entityConditionsMaxLengthIsNotValid")
    private String entityConditions;
    private Integer fieldCount;
    private String fieldTitle;
    private Integer fromVersion;
    @NotNull(message = "error.formEntity.typeIdIsNotValid")
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

    public String getFromEntity() {
        return fromEntity;
    }

    public void setFromEntity(String fromEntity) {
        this.fromEntity = fromEntity;
    }

    public String getEntityConditions() {
        return entityConditions;
    }

    public void setEntityConditions(String entityConditions) {
        this.entityConditions = entityConditions;
    }

    public Integer getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(Integer fieldCount) {
        this.fieldCount = fieldCount;
    }

    public String getFieldTitle() {
        return fieldTitle;
    }

    public void setFieldTitle(String fieldTitle) {
        this.fieldTitle = fieldTitle;
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
