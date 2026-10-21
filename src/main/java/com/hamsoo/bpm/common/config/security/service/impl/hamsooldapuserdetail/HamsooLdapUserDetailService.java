package com.hamsoo.bpm.common.config.security.service.impl.hamsooldapuserdetail;

import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.oauthprovider.OauthProvider;
import com.hamsoo.bpm.model.entity.organ.Organ;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import com.hamsoo.bpm.service.api.organ.OrganService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class HamsooLdapUserDetailService implements UserDetailsService {

    private final JwtTokenProvider jwtTokenProvider;
    private final OauthProviderService oauthProviderService;
    private final AccountService accountService;
    private final OrganService organService;

    public HamsooLdapUserDetailService(JwtTokenProvider jwtTokenProvider, OauthProviderService oauthProviderService, AccountService accountService, OrganService organService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oauthProviderService = oauthProviderService;
        this.accountService = accountService;
        this.organService = organService;
    }


    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        String curOrgan = jwtTokenProvider.getCurOrganFromRequest(request);
        Long id=null;
        if (StringUtils.hasText(curOrgan)) {
            try {
                id = Long.valueOf(curOrgan);
            } catch (NumberFormatException e) {

            }
        }
        if(id!=null){
            Optional<OauthProvider> oauthProvider = oauthProviderService.findByOrganIdAndType(id, OauthProvider.providerType.LDAP.name());
            if (oauthProvider.isPresent()) {
            return  loadUserByUsername(username,oauthProvider.get(),curOrgan);
            }
        }
         return null;
    }

    public UserDetails loadUserByUsername(String username, OauthProvider oauthProvider,String curOrgan) {

        Optional<Account> accountOptional = accountService.findByProviderAndProviderUsername(
                oauthProvider.getRegisterationId(),
                username + "_" + oauthProvider.getRegisterationId());
        Optional<OrganDto> organDto = Optional.empty();
        if (StringUtils.hasText(curOrgan)) {
            Optional<Organ> organ = organService.findById(Long.valueOf(curOrgan));
            if (organ.isPresent())
                organDto = Optional.of(organService.mapToDto(organ.get()));
        }
        AccountDto account = null;
        if (!accountOptional.isPresent()) {
            account = registerNewAccount(username, oauthProvider);
        } else {
            account = accountService.mapToDtoByPasswordAndUserAndRoles(accountOptional.get());
            if (account.getUserDto() != null) {
                return new HamsooUserDetail( account.getUserDto(),account, organDto.orElse(null));
            }
        }
        return new HamsooUserDetail(null, account, organDto.orElse(null));
    }

    private AccountDto registerNewAccount(String username, OauthProvider oauthProvider) {

        if (oauthProvider != null) {
            Account account = new Account();
            account.setProvider(oauthProvider);
            account.setProviderUsername(username + "_" + oauthProvider.getRegisterationId());
            account.setUsername(account.getProviderUsername());
            account.setPassword(account.getProviderUsername());
            account.setEnabled(true);
            account = accountService.save(account);
            return accountService.mapToDtoByPasswordAndUserAndRoles(account);
        }
        return null;
    }

}

