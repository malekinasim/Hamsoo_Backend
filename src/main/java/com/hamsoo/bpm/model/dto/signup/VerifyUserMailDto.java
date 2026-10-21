package com.hamsoo.bpm.model.dto.signup;


import javax.validation.constraints.NotNull;

public class VerifyUserMailDto {
    @NotNull(message = "error.verifyMail.mailIsNotNull")
    // @Email(message = "error.verifyMail.mailIsNotValid")
    private String mail;
    @NotNull(message = "error.verifyMail.verifyCodeIsNotValid")
    private String verifyCode;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
