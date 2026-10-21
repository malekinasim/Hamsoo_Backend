package com.hamsoo.bpm.model.dto.account;

import com.hamsoo.bpm.model.dto.signup.validator.ValidPassword;

import javax.validation.constraints.NotBlank;

public class ChangePasswordDto {

    @NotBlank(message = "error.user.oldPasswordIsNotValid")
    private String oldPassword;

    @NotBlank(message = "error.user.passwordIsNotValid")
    @ValidPassword(message = "error.invalid_password_policy")
    private String password;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
