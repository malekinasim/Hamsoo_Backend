package com.hamsoo.bpm.model.dto.jwt;

import com.hamsoo.bpm.model.dto.signup.validator.ValidPassword;

import javax.validation.constraints.NotBlank;

public class ForgetPasswordDto {
    @NotBlank(message = "error.user.passwordIsNotValid")
    @ValidPassword(message = "error.invalid_password_policy")
    private String password;

    @NotBlank(message = "error.resetPass.mobileNotValid")
    private String mobile;

    @NotBlank(message = "error.resetPass.nationalCodeNotValid")
    private String nationalcode;

    private String code;

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

    public String getNationalcode() {
        return nationalcode;
    }

    public void setNationalcode(String nationalcode) {
        this.nationalcode = nationalcode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
