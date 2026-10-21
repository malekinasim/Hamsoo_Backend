package com.hamsoo.bpm.repository.jpa.accounttype;

import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.accounttoken.AccountToken;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(rollbackFor = Exception.class)
public interface AccountTokenRepository extends HamsooRepository<AccountToken, Long> {

    Optional<AccountToken> findByAccountAndTokenTypeAndDestinationTypeAndToken(Account account, AccountToken.TokenType tokenType,
                                                                               AccountToken.DestinationType destinationType, String token);

    Optional<AccountToken> findByDestinationAndTokenTypeAndDestinationTypeAndToken(String destination, AccountToken.TokenType tokenType,
                                                                                   AccountToken.DestinationType destinationType, String token);


}
