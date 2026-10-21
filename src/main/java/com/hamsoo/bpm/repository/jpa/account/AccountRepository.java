package com.hamsoo.bpm.repository.jpa.account;

import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.repository.jpa.HamsooRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(rollbackFor = Exception.class)
public interface AccountRepository extends HamsooRepository<Account, Long> {


    Optional<Account> findByUsernameIgnoreCaseAndEnabled(String username, boolean b);

    Optional<Account> findByProvider_RegisterationIdAndUser_MobileAndUser_NationalCode(String registerId, String mobile, String nationalCode);


    @Query(value = "select account from  Account  account " +
            " where account.provider.registerationId=:registerId" +
            " and (" +
            " account.user.nationalCode=:natCode)")
    Optional<Account> findHamsooAccountByNationalCode(
            @Param("registerId") String registerId,
            @Param("natCode") String natCode);


    Optional<Account> findByProvider_RegisterationIdAndProviderUsername(String provider, String username);


    Optional<Account> findByProvider_RegisterationIdAndUsernameIgnoreCase(String provider, String username);


    Optional<Account> findByProvider_RegisterationIdAndUserIdAndEnabled(String provider, Long userId, Boolean enable);


    @Query(value = "select account from  Account  account " +
            " where  (" +
            " account.user.nationalCode=:natCode)")
    List<Account> findALLAccountByNationalCod(@Param("natCode") String natCode);

    @Query(value = "select account from  Account  account " +
            "inner join account.user u " +
            "inner join account.provider pr " +
            "where u.id=:userId and pr.registerationId=:provider")
    Optional<Account> findHamsooAccountByUserId(Long userId, String provider);

    @Query(value = "select account from  Account  account " +
            "inner join account.user u " +
            "inner join account.provider pr " +
            "where u.id=:userId and pr.registerationId <> :provider")
    List<Account> findNonHamsooAccountByUserId(Long userId, String provider);
}
