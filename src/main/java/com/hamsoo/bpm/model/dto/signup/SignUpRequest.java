package com.hamsoo.bpm.model.dto.signup;

import com.hamsoo.bpm.model.dto.signup.validator.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignUpRequest {

    @NotBlank(message = "error.user.usernameIsNotValid")
    private String username;

    @NotBlank(message = "error.user.nameIsNotValid")
    private String name;

    @NotBlank(message = "error.user.familyIsNotValid")
    private String family;

    @NotBlank(message = "error.user.emailIsNotBlank")
    @Email(message = "error.user.emailIsNotValid")
    private String mail;

    @NotBlank(message = "error.user.passwordIsNotBlank")
    @ValidPassword(message = "error.invalid_password_policy")
    private String password;


    @NotBlank(message = "error.user.passwordIsNotBlank")
    // @ValidNationalCode(message = "error.invalid_nationalCode")
    private String nationalCode;

    @NotBlank(message = "error.user.mobileIsNotBlank")
    private String mobile;

    private String code;

    private Long userId;

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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
