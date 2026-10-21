package com.hamsoo.bpm.model.dto.jwt;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class JwtRequest implements Serializable {


    @NotBlank(message = "error.user.usernameIsNotValid")
    private String username;
    @NotBlank(message = "error.user.passwordIsNotValid")
    private String password;

    private Long curOrgan;
    //need default constructor for JSON Parsing
    public JwtRequest() {

    }

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCurOrgan() {
        return curOrgan;
    }

    public void setCurOrgan(Long curOrgan) {
        this.curOrgan = curOrgan;
    }
}

