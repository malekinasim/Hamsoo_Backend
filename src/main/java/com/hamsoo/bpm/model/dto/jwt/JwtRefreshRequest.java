package com.hamsoo.bpm.model.dto.jwt;

public class JwtRefreshRequest {
    //@NotBlank
    private String refreshToken;
    private Long curOrgan;
    private String originalUser;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getCurOrgan() {
        return curOrgan;
    }

    public void setCurOrgan(Long curOrgan) {
        this.curOrgan = curOrgan;
    }

    public String getOriginalUser() {
        return originalUser;
    }

    public void setOriginalUser(String originalUser) {
        this.originalUser = originalUser;
    }

}
