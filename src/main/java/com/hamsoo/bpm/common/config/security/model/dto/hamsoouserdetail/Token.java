package com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail;

import java.time.LocalDateTime;

public class Token {

    private String tokenValue;
    private Long duration;
    private LocalDateTime expiryDate;

    public Token(String tokenValue, Long duration, LocalDateTime expiryDate) {
        this.tokenValue = tokenValue;
        this.duration = duration;
        this.expiryDate = expiryDate;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
