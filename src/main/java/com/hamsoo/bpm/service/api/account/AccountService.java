package com.hamsoo.bpm.service.api.account;

import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.account.ChangePasswordDto;
import com.hamsoo.bpm.model.dto.jwt.ForgetPasswordDto;
import com.hamsoo.bpm.model.dto.signup.RegisterRequest;
import com.hamsoo.bpm.model.dto.signup.SignUpRequest;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.accounttoken.AccountToken;
import com.hamsoo.bpm.repository.jpa.account.AccountRepository;
import com.hamsoo.bpm.service.api.base.BaseService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountService extends BaseService<Account, Long, AccountRepository> {

    Account findByIdOrElseThrow(Long id);

    Account ChangePassHamsooAccount(String username, ChangePasswordDto changePasswordDto);


    Optional<Account> findByProviderAndProviderUsername(String provider, String userName);


    Optional<Account> findByUsername(String username);

    Account findHamsooAccountByUserIdOrElseThrow(Long userId);

    Map<String, String> sendResetPassTokenSMS(String mobile);

    AccountToken checkResetPassToken(String nationalcode, String mobile, String token);

    Boolean ChangePass(ForgetPasswordDto changePass);

    String sendSignUpTokenSMS(String mobile, String nationalCode);
    // String sendSignUpTokenSMS(String mobile, String nationalCode, String token);

    Map<String, Object> checkSignupCode(String mobile, String nationalCode, String code);

    Account registerHamsooAccount(SignUpRequest signUpRequest);


    AccountDto mapToDto(Account account);

    Account addUserToAccount(String mobile, String nationalCode, Long accountId);

    Account registerUser(Long accountId, RegisterRequest signUpRequest);


    Optional<Account> findHamsooAccountByUser(Long userId);

    List<Account> findNonHamsooAccountByUser(Long userId);

    AccountDto mapToDtoByPasswordAndUserAndRoles(Account account);
}