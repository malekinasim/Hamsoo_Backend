package com.hamsoo.bpm.service.impl.refreshtoken;

import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.refreshtoken.RefreshToken;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.refreshtoken.RefreshTokenService;
import com.hamsoo.bpm.repository.jpa.refreshtoken.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceimpl implements RefreshTokenService {
    @Value("${hamsoo.jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;
    private RefreshTokenRepository refreshTokenRepository;
    private AccountService accountService;

    public RefreshTokenServiceimpl(RefreshTokenRepository refreshtokenRepository, AccountService accountService) {
        this.refreshTokenRepository = refreshtokenRepository;
        this.accountService = accountService;
    }

    @Override
    public RefreshTokenRepository getRepository() {
        return refreshTokenRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(Long accountId) {
        Date now = new Date(System.currentTimeMillis());
        Long duration = now.getTime() + refreshTokenDurationMs;
        Date expireDate = new Date(duration);
        Account account = accountService.findByIdOrElseThrow(accountId);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccount(account);
        refreshToken.setExpiryDate(LocalDateTime.ofInstant(expireDate.toInstant(), ZoneId.systemDefault()));
        refreshToken.setDuration(refreshTokenDurationMs);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        deleteAllExpiredByAccountId(account.getId());
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(LocalDateTime.now()) < 0) {
            refreshTokenRepository.delete(token);

            return null;
        }
        return token;
    }

    @Override
    public int deleteAllExpiredByAccountId(Long id) {
        return refreshTokenRepository.deleteAllByAccount_IdAndExpiryDateBefore(id,LocalDateTime.now());
    }

    @Override
    public int deleteByToken(String Token) {
        return refreshTokenRepository.deleteByToken(Token);
    }
}
