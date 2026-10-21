package com.hamsoo.bpm.model.entity.fcm;

import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.model.entity.base.AuditEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_fcm_tokens", schema = "bpm", catalog = "BPM_DB")
public class FcmToken extends AuditEntity<Long> {

    private Long id;
    private User user;
    private String token;
    private LocalDateTime timestamp;
    private String os;
    private String browser;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "fcm_token_id")
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Basic
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Basic
    @Column(name = "timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "os")
    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    @Basic
    @Column(name = "browser")
    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

}
