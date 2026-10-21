package com.hamsoo.bpm.model.dto.user;

import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.role.RoleDto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class UserDto {
    private String username;
    @NotBlank(message = "error.user.nameIsNotValid")
    private String name;
    @NotBlank(message = "error.user.familyIsNotValid")
    private String family;
    // @NotBlank(message = "error.user.emailIsNotBlank")
    @Email(message = "error.user.emailIsNotValid")
    private String email;
    //@NotBlank(message = "error.user.mobileIsNotBlank")
    private String mobile;
    private String nationalCode;
    private Long id;
    private Long imageId;
    private Boolean isComplated;
    private List<RoleDto> roles;
    private List<OrganDto> organs;
    private OrganDto curOrgan;


    private String fullName;

    public UserDto() {
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getComplated() {
        return isComplated;
    }

    public void setComplated(Boolean complated) {
        isComplated = complated;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }

    public List<OrganDto> getOrgans() {
        return organs;
    }

    public void setOrgans(List<OrganDto> organs) {
        this.organs = organs;
    }

    public OrganDto getCurOrgan() {
        return curOrgan;
    }

    public void setCurOrgan(OrganDto curOrgan) {
        this.curOrgan = curOrgan;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
