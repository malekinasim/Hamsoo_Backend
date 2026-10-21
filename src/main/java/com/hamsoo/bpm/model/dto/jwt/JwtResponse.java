package com.hamsoo.bpm.model.dto.jwt;


import java.io.Serializable;

public class JwtResponse implements Serializable {


    private final Long maxAge;
    private Long organId;
    private String token;
    private String type = "Bearer";
    private String refreshToken;

    public JwtResponse(Long maxAge, Long organId, String token, String refreshToken) {
        this.maxAge = maxAge;
        this.organId = organId;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}