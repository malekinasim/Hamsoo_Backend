package com.hamsoo.bpm.model.dto.signup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class RegisterRequest {


    @NotBlank(message = "error.user.nameIsNotValid")
    private String name;

    @NotBlank(message = "error.user.familyIsNotValid")
    private String family;

    @NotBlank(message = "error.user.emailIsNotBlank")
    @Email(message = "error.user.emailIsNotValid")
    private String mail;


    @NotBlank(message = "error.user.passwordIsNotBlank")
    // @ValidNationalCode(message = "error.invalid_nationalCode")
    private String nationalCode;

    @NotBlank(message = "error.user.mobileIsNotBlank")
    private String mobile;


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


}
