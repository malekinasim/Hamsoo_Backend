package com.hamsoo.bpm.common.config.security.service.impl.hamsoouserdetail;

import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.HamsooUserDetail;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.model.dto.account.AccountDto;
import com.hamsoo.bpm.model.dto.organ.OrganDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.model.entity.rolemember.RoleMember;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.role.RoleService;
import com.hamsoo.bpm.service.api.rolemember.RoleMemberService;
import com.hamsoo.bpm.service.api.userorgan.UserOrganService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HamsooUserDetailsService implements UserDetailsService {

    private final AccountService accountService;
    private final RoleService roleService;
    private final UserOrganService userOrganService;
    private final RoleMemberService roleMemberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    public HamsooUserDetailsService(AccountService accountService,
                                    RoleMemberService roleMemberService,
                                    UserOrganService userOrganService,
                                    JwtTokenProvider jwtTokenProvider,
                                    RoleService roleService) {
        this.accountService = accountService;
        this.roleMemberService = roleMemberService;
        this.userOrganService = userOrganService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleService = roleService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        String curOrgan = jwtTokenProvider.getCurOrganFromRequest(request);
        return loadUserByUsername(username, curOrgan);
    }

    public UserDetails loadUserByUsername(String username, String curOrgan) {
        Account account = accountService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        AccountDto accountDto = accountService.mapToDtoByPasswordAndUserAndRoles(account);
        if (accountDto.getUserDto() == null) {
            return new HamsooUserDetail(null, accountDto, null);
        }
        OrganDto organDto = userOrganService.getCurUserOrgan(curOrgan, accountDto.getUsername());
        accountDto.getUserDto().setCurOrgan(organDto);

        //OrganBased role by current Organ
        List<RoleMember> roleMembers = new ArrayList<>();
        roleMembers.addAll(roleMemberService.findAllByUserAndOrgan(accountDto.getUserDto().getId(), organDto.getId()));
        //add applicationBased role
        roleMembers.addAll(roleMemberService.findAllAppBasedRoleByUser(accountDto.getUserDto().getId()));

        accountDto.getUserDto().setRoles(roleMembers.stream()
                .map(roleMem ->
                        roleService.mapToDtoByResourceInfo(roleMem.getRole(), organDto.getId())
                ).collect(Collectors.toList()));

        return new HamsooUserDetail(accountDto.getUserDto(), accountDto, organDto);
    }
}
