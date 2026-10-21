package com.hamsoo.bpm.model.dto.modeldocument;

import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class ModelDocumentDto {

    private Long id;
    @NotNull(message = "error.modelDocument.modelVersionIdIsNotValid")
    private Long modelVersionId;
    private FilesDto filesDto;
    @NotBlank(message = "error.modelDocument.nameIsNotValid")
    @Size(max = 100, message = "error.modelDocument.nameMaxLengthIsNotValid")
    private String name;
    @Size(max = 500, message = "error.modelDocument.descriptionMaxLengthIsNotValid")
    private String description;

    private String elementId;
    private UserDto createBy;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public ModelDocumentDto() {
    }

    public ModelDocumentDto(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public ModelDocumentDto(Long modelVersionId, FilesDto filesDto) {
        this.modelVersionId = modelVersionId;
        this.filesDto = filesDto;
    }

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

    public FilesDto getFilesDto() {
        return filesDto;
    }

    public void setFilesDto(FilesDto filesDto) {
        this.filesDto = filesDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public UserDto getCreateBy() {
        return createBy;
    }

    public void setCreateBy(UserDto createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
