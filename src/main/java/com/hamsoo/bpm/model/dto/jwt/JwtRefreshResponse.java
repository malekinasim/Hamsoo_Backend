package com.hamsoo.bpm.model.dto.jwt;

import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.Token;

public class JwtRefreshResponse {
    private Token accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public JwtRefreshResponse(Token accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

    }

    public Token getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Token accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }


}
