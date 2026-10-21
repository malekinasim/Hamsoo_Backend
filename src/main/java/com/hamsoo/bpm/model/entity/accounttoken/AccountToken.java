package com.hamsoo.bpm.model.entity.accounttoken;

import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.base.TimestampEntity;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "bpm_account_tokens", schema = "bpm", catalog = "BPM_DB")
public class AccountToken extends TimestampEntity<Long> {
    private Long id;
    private LocalDateTime expiryDate;
    private TokenType tokenType;
    private DestinationType destinationType;
    private String token;
    private Account account;
    private Boolean isUsed = false;
    private String resetCookieVal;
    private String destination;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "expiry_date")
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }


    @Basic
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "destination_type")
    public DestinationType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    @Basic

    @Column(name = "destination")
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    @Basic
    @Column(name = "is_used")
    public Boolean getUsed() {
        return isUsed;
    }

    @Basic
    @Column(name = "reset_cookie_val")
    public String getResetCookieVal() {
        return resetCookieVal;
    }

    public void setResetCookieVal(String resetCookieVal) {
        this.resetCookieVal = resetCookieVal;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public enum TokenType {
        RESET_PASS, SIENGUP, VERIFY_MAIL
    }

    public enum DestinationType {
        MAIL, SMS
    }


}