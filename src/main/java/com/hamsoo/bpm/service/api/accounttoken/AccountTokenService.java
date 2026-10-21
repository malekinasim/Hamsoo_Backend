package com.hamsoo.bpm.service.api.accounttoken;

import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.accounttoken.AccountToken;
import com.hamsoo.bpm.repository.jpa.accounttype.AccountTokenRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.time.LocalDateTime;


public interface AccountTokenService extends BaseService<AccountToken, Long, AccountTokenRepository> {
    AccountToken findByIdOrElseThrow(Long id);

    AccountToken generateToken(Account account, AccountToken.TokenType tokenType, AccountToken.DestinationType destinationType);

    AccountToken generateToken(AccountToken.TokenType tokenType, AccountToken.DestinationType destinationType, String destiination);

    AccountToken validateToken(Account account, AccountToken.TokenType resetPass, AccountToken.DestinationType sms, String token, LocalDateTime now);


    AccountToken validateToken(String destination, AccountToken.TokenType resetPass, AccountToken.DestinationType sms, String token, LocalDateTime now);

}
