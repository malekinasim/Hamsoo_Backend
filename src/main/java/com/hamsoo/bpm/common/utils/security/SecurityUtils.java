package com.hamsoo.bpm.common.utils.security;

import com.hamsoo.bpm.common.config.security.custompremission.CustomMethodSecurityExpression;
import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class SecurityUtils {
    private static AccountService accountService = null;
    private static UserService userService = null;

    private SecurityUtils(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    public static Authentication getAuthentication() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        return authentication;
    }

    public static Optional<UserDto> getCurrentUser() {
//        UserDetails userDetails=  getImpersonator();
//      if(userDetails!=null){
//          Optional<Account> account= accountService.findByUsername(userDetails.getUsername());
//          if(account.isEmpty())  return Optional.empty();
//          return Optional.of(userService.m(account.get().getUser(),account.get()));
//      }else {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof HamsooUserDetail)) {
            return Optional.empty();
        }
        HamsooUserDetail hamsooUserDetail = (HamsooUserDetail) principal;
        if (hamsooUserDetail.getUser() == null) {
            return Optional.empty();
        }
        return Optional.of(hamsooUserDetail.getUser());
        //  }
    }

    public static Optional<AccountDto> getCurrentAccount() {
//        UserDetails userDetails=  getImpersonator();
//        if(userDetails!=null){
//           Optional<Account> account= accountService.findByUsername(userDetails.getUsername());
//           if(account.isEmpty())  return Optional.empty();
//           return Optional.of(accountService.mapToDto(account.get()));
//        }else {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof HamsooUserDetail)) {
            return Optional.empty();
        }
        HamsooUserDetail hamsooUserDetail = (HamsooUserDetail) principal;
        return Optional.of(hamsooUserDetail.getAccount());
        //}
    }

    public static Optional<OrganDto> getCurrentOrgan() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof HamsooUserDetail)) {
            return Optional.empty();
        }
        HamsooUserDetail hamsooUserDetail = (HamsooUserDetail) principal;
        if (hamsooUserDetail.getCurrentOrgan() == null) {
            return Optional.empty();
        }
        return Optional.of(hamsooUserDetail.getCurrentOrgan());
    }

    public static Long getCurrentOrganIdOrElseThrow() {
        return getCurrentOrgan()
                .orElseThrow(() -> new BusinessException("error.security.currentOrganNotFound")).getId();
    }

    public static UserDto getCurrentUserOrElseThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new BusinessException("error.security.currentUserNotFound"));
    }

    public static AccountDto getCurrentAccountOrElseThrow() {
        return getCurrentAccount()
                .orElseThrow(() -> new BusinessException("error.security.currentUserNotFound"));
    }

    public static Optional<String> getUsernameOfCurrentUser() {
        return getCurrentAccount().map(AccountDto::getUsername);
    }

    public static Long getUserIdOfCurrentUserOrElseThrow() {
        return getCurrentUserOrElseThrow().getId();
    }


    public static boolean isImpersonating() {
        return (getImpersonator() != null);
    }

    public static UserDetails getImpersonator() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            return null;
        }

        Authentication authWorkFor = securityContext.getAuthentication();
        if (authWorkFor == null) {
            return null;
        }

        Authentication authActual = getSourceAuthentication(authWorkFor);
        if (authActual == null) {
            return null;
        }

        return (UserDetails) authActual.getPrincipal();
    }

    private static Authentication getSourceAuthentication(Authentication current) {
        Authentication original = null;

        // iterate over granted authorities and find the 'switch user' authority
        Collection<? extends GrantedAuthority> authorities = current.getAuthorities();

        for (GrantedAuthority auth : authorities) {
            // check for switch user type of authority
            if (auth instanceof SwitchUserGrantedAuthority) {
                original = ((SwitchUserGrantedAuthority) auth).getSource();
            }
        }

        return original;
    }

    public static boolean hasAuthority(String authority){
        CustomMethodSecurityExpression root=new CustomMethodSecurityExpression(getAuthentication());
        return root.hasAuthority(authority);
    }

    public static boolean hasAnyAuthority(String... authorities){
        CustomMethodSecurityExpression root=new CustomMethodSecurityExpression(getAuthentication());
        return root.hasAnyAuthority(authorities);
    }

}
