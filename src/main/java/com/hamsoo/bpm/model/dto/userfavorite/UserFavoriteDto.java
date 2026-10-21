package com.hamsoo.bpm.model.dto.userfavorite;

import com.hamsoo.bpm.model.dto.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserFavoriteDto {

    private Long id;
    @NotBlank(message = "error.userFavorite.referenceTypeIsNotValid")
    private String referenceType;
    @NotNull(message = "error.userFavorite.referenceIdIsNotValid")
    private Long referenceId;

    //return property
    private UserDto userDto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
