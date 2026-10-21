package com.hamsoo.bpm.controller.account;

import com.hamsoo.bpm.common.exception.BusinessException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.account.ChangePasswordDto;
import com.hamsoo.bpm.model.entity.account.Account;
import com.hamsoo.bpm.service.api.account.AccountService;
import com.hamsoo.bpm.service.api.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    //----------get current User---------

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        if (SecurityUtils.getUsernameOfCurrentUser().isPresent()) {
            Account account = accountService.findByUsername(SecurityUtils.getUsernameOfCurrentUser().get())
                    .orElseThrow(() -> new BusinessException("error.curUser.notFound", SecurityUtils.getUsernameOfCurrentUser().get()));
            return new ResponseEntity<>(userService.mapToDtoByRoleInfoAndOrgans(account.getUser(), account), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    //----------get changePassword---------
    @PutMapping("/hamsoo/changePass")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> ChangePassHamsooAccount(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        if (SecurityUtils.getCurrentAccount().isPresent()) {
            Account account = accountService.ChangePassHamsooAccount(SecurityUtils.getCurrentAccount().get().getUsername(), changePasswordDto);
            return new ResponseEntity<>(userService.mapToDto(account), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
