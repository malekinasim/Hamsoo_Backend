package com.hamsoo.bpm.common.config.security.filter;

import com.hamsoo.bpm.common.config.security.handler.SwitchUserAuthenticationSuccessHandler;
import com.hamsoo.bpm.common.config.security.service.impl.hamsoouserdetail.HamsooUserDetailsService;
import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public class HamsooSwitchUserFilter extends SwitchUserFilter {
    private final UserOrganService userOrganService;
    private final AccountService accountService;


    public HamsooSwitchUserFilter(HamsooUserDetailsService hamsooUserDetailsService,
                                  UserOrganService userOrganService,
                                  AccountService accountService, SwitchUserAuthenticationSuccessHandler
                                          successHandler) {
        super();
        this.accountService = accountService;
        this.userOrganService = userOrganService;
        this.setUserDetailsService(hamsooUserDetailsService);
        this.setUsernameParameter("username");
        this.setSwitchUserUrl("/api/v1/switch_user");
        this.setExitUserUrl("/api/v1/switch_user_exit");
        //  this.setTargetUrl("/api/v1/auth/switchRedirect");
        this.setSuccessHandler(successHandler);
    }

    @Override
    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        String organId = request.getParameter("organId");
        String username = null;
        if (StringUtils.hasText(organId)) {
            List<User> adminUsersOfOrgan = userOrganService.getAdminUsersOfOrgan(Long.valueOf(organId));
            if (!CollectionUtils.isEmpty(adminUsersOfOrgan)) {
                User user = adminUsersOfOrgan.get(0);
                Optional<Account> account = accountService.findHamsooAccountByUser(user.getId());
                if (account.isPresent())
                    username = account.get().getUsername();
                else {
                    List<Account> accounts = accountService.findNonHamsooAccountByUser(user.getId());
                    if (!CollectionUtils.isEmpty(accounts))
                        username = accounts.get(0).getUsername();
                }
            }else{
                throw new BusinessException("error.organ.switchToOrganAdmin.organAdminIsNull");
            }

        }
        CustomHttpServletRequest customHttpServletRequest = new CustomHttpServletRequest(request);
        customHttpServletRequest.addHeader("username", username);


        return super.attemptSwitchUser(customHttpServletRequest);
    }

    @Override
    protected Authentication attemptExitUser(HttpServletRequest request) throws AuthenticationCredentialsNotFoundException {
        return super.attemptExitUser(request);
    }
}
