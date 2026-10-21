package com.hamsoo.bpm.model.dto.modelcomment;

import com.hamsoo.bpm.model.dto.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class ModelCommentDto {

    private Long id;
    private Long userId;
    @NotBlank(message = "error.modelComment.textIsNotValid")
    @Size(max = 1000, message = "error.modelComment.textMaxLengthIsNotValid")
    private String text;
    @NotNull(message = "error.modelComment.modelVersionIdIsNotValid")
    private Long modelVersionId;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    //Return property
    private UserDto userDto;
    private String elementId;
    private Long modelElementReferenceId;
    private LocalDateTime modelElementReferenceModifiedDate;

    //search property
    private String searchText;

    public ModelCommentDto() {
    }

    public ModelCommentDto(Long id, String text, String name, String family, LocalDateTime modifiedDate,
                           Long modelElementReferenceId, LocalDateTime modelElementReferenceModifiedDate,
                           LocalDateTime createdDate, String elementId) {
        this.id = id;
        this.text = text;
        this.userDto = new UserDto();
        this.userDto.setName(name);
        this.userDto.setFamily(family);
        this.modifiedDate = modifiedDate;
        this.createdDate = createdDate;
        this.elementId = elementId;
        this.modelElementReferenceId = modelElementReferenceId;
        this.modelElementReferenceModifiedDate = modelElementReferenceModifiedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(Long modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
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

    public Long getModelElementReferenceId() {
        return modelElementReferenceId;
    }

    public void setModelElementReferenceId(Long modelElementReferenceId) {
        this.modelElementReferenceId = modelElementReferenceId;
    }

    public LocalDateTime getModelElementReferenceModifiedDate() {
        return modelElementReferenceModifiedDate;
    }

    public void setModelElementReferenceModifiedDate(LocalDateTime modelElementReferenceModifiedDate) {
        this.modelElementReferenceModifiedDate = modelElementReferenceModifiedDate;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
