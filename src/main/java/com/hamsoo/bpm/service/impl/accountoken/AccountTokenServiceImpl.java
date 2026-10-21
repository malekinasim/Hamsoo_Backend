package com.hamsoo.bpm.service.impl.accountoken;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.accounttoken.AccountToken;
import com.hamsoo.bpm.repository.jpa.accounttype.AccountTokenRepository;
import com.hamsoo.bpm.service.api.accounttoken.AccountTokenService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountTokenServiceImpl implements AccountTokenService {
    @Value("${hamsoo.resetpass.token.mail.expiration}")
    long RestPassMailExpirtion;
    @Value("${hamsoo.resetpass.token.sms.expiration}")
    long ResetPassSmsExpiration;

    @Value("${hamsoo.signup.token.sms.expiration}")
    long SignupSmsExpiration;

    @Value("${hamsoo.signup.token.sms.expiration}")
    long VerifyMailExpiration;

    private AccountTokenRepository accountTokenRepository;

    public AccountTokenServiceImpl(AccountTokenRepository accountTokenRepository) {
        this.accountTokenRepository = accountTokenRepository;
    }

    @Override
    public AccountTokenRepository getRepository() {
        return accountTokenRepository;
    }

    @Override
    public AccountToken findByIdOrElseThrow(Long id) {
        if (id == null) {
            throw new BusinessException("error.accounttoken.idIsNotValid");
        }
        return findById(id).orElseThrow(() -> new BusinessException("error.accounttoken.idIsNotValid"));
    }


    @Override
    public AccountToken generateToken(Account account, AccountToken.TokenType tokenType, AccountToken.DestinationType destinationType) {
        AccountToken accountToken = new AccountToken();
        accountToken.setDestinationType(destinationType);
        accountToken.setTokenType(tokenType);
        accountToken.setAccount(account);
//        accountToken.setCreatedBy(account.getUsername());
//        accountToken.setModifiedBy(account.getUsername());
        do {
            String token = getToken(destinationType);
            Optional<AccountToken> tokenOptional = getRepository().findByAccountAndTokenTypeAndDestinationTypeAndToken(account, tokenType,
                    destinationType, token);
            if (!tokenOptional.isPresent())
                accountToken.setToken(token);

        } while (accountToken.getToken() == null);


        accountToken.setExpiryDate(getTokenExpiryDate(tokenType, destinationType));
        try {
            return this.save(accountToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("error.accountToken.notPersists");
        }
    }

    @Override
    public AccountToken generateToken(AccountToken.TokenType tokenType, AccountToken.DestinationType destinationType, String destination) {
        AccountToken accountToken = new AccountToken();
        accountToken.setDestinationType(destinationType);
        accountToken.setTokenType(tokenType);
        do {
            String token = getToken(destinationType);
            Optional<AccountToken> tokenOptional = getRepository().findByDestinationAndTokenTypeAndDestinationTypeAndToken(destination, tokenType, destinationType, token);
            if (!tokenOptional.isPresent())
                accountToken.setToken(token);

        } while (accountToken.getToken() == null);

        accountToken.setExpiryDate(getTokenExpiryDate(tokenType, destinationType));
        accountToken.setDestination(destination);
//        if(SecurityUtils.getCurrentAccount().isPresent()){
//            accountToken.setCreatedBy(SecurityUtils.getCurrentUserOrElseThrow());
//            accountToken.setModifiedBy(SecurityUtils.getCurrentUserOrElseThrow());
//        }else{
//            accountToken.setCreatedBy("");
//            accountToken.setModifiedBy("");
//        }
        try {
            return this.save(accountToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("error.accountToken.notPersists");
        }
    }

    private String getToken(AccountToken.DestinationType destinationType) {
        String token;
        if (destinationType.equals(AccountToken.DestinationType.MAIL)) {
            token = UUID.randomUUID().toString();
        } else {//SMS
            token = RandomStringUtils.randomNumeric(5);
        }
        return token;
    }

    private LocalDateTime getTokenExpiryDate(AccountToken.TokenType tokenType, AccountToken.DestinationType destinationType) {
        LocalDateTime expiryDate;
        Date now = new Date(System.currentTimeMillis());
        if (tokenType.equals(AccountToken.TokenType.RESET_PASS)) {
            if (destinationType.equals(AccountToken.DestinationType.MAIL)) {
                Long duration = now.getTime() + RestPassMailExpirtion;
                expiryDate = LocalDateTime.ofInstant((new Date(duration)).toInstant(), ZoneId.systemDefault());
            } else {//SMS
                Long duration = now.getTime() + RestPassMailExpirtion;
                expiryDate = LocalDateTime.ofInstant((new Date(duration)).toInstant(), ZoneId.systemDefault());
            }
        } else if (tokenType.equals(AccountToken.TokenType.SIENGUP)) {//just by SMS
            Long duration = now.getTime() + SignupSmsExpiration;
            expiryDate = LocalDateTime.ofInstant((new Date(duration)).toInstant(), ZoneId.systemDefault());

        } else { //VERIFY_MAIL just by mail
            Long duration = now.getTime() + VerifyMailExpiration;
            expiryDate = LocalDateTime.ofInstant((new Date(duration)).toInstant(), ZoneId.systemDefault());
        }
        return expiryDate;
    }

    @Override
    public AccountToken validateToken(Account account, AccountToken.TokenType tokenType,
                                      AccountToken.DestinationType destinationType, String token, LocalDateTime now) {

        Optional<AccountToken> tokenOptional = getRepository().findByAccountAndTokenTypeAndDestinationTypeAndToken(
                account,
                tokenType,
                destinationType, token);

        return getAccountToken(tokenOptional);

    }

    @Override
    public AccountToken validateToken(String destination, AccountToken.TokenType tokenType,
                                      AccountToken.DestinationType destinationType, String token, LocalDateTime now) {
        Optional<AccountToken> tokenOptional = getRepository().findByDestinationAndTokenTypeAndDestinationTypeAndToken(
                destination,
                tokenType,
                destinationType, token);
        return getAccountToken(tokenOptional);

    }

    private AccountToken getAccountToken(Optional<AccountToken> tokenOptional) {
        if (!tokenOptional.isPresent()) throw new BusinessException("error.code.notValid");

        if (!tokenOptional.get().getExpiryDate().isAfter(LocalDateTime.now()))
            throw new BusinessException("error.code.expire");
        tokenOptional.get().setUsed(true);
        this.save(tokenOptional.get());
        return tokenOptional.get();
    }


}
