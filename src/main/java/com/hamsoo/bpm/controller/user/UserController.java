package com.hamsoo.bpm.controller.user;

import com.hamsoo.bpm.common.exception.ValidationException;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.files.FilesDto;
import com.hamsoo.bpm.model.dto.search.SearchDto;
import com.hamsoo.bpm.model.dto.user.UserDto;
import com.hamsoo.bpm.model.entity.user.User;
import com.hamsoo.bpm.service.api.user.UserService;
import com.hamsoo.bpm.validator.user.UserImageValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/")

public class UserController {

    private final UserService userService;
    private final UserImageValidator userImageValidator;

    public UserController(UserService userService, UserImageValidator userImageValidator) {
        this.userService = userService;
        this.userImageValidator = userImageValidator;
    }


    //----------get by id---------
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        User user = userService.findByIdOrElseThrow(id);
        if (user != null) return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        return new ResponseEntity<>(userService.mapToDto(user), HttpStatus.OK);
    }


    //----------get All---------
    @GetMapping(value = "/user/list")
    public ResponseEntity<?> listAllUser(@RequestParam int page, @RequestParam int size
            , @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "false") Boolean filterOrgan) {
        SearchDto userPage = userService.findAll(filter, filterOrgan, PageRequest.of(page, size));
        return new ResponseEntity<>(userPage, HttpStatus.OK);

    }

    @GetMapping(value = "/user/withoutRole/list")
    @PreAuthorize("hasAnyAuthority(@resourceItemService.ORGAN_MEMBER_MNG)")
    public ResponseEntity<?> listAllUserWithoutRole(@RequestParam int page, @RequestParam int size
            , @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "false") Boolean filterOrgan) {
        SearchDto userPage = userService.listWithoutRole(filter, filterOrgan, PageRequest.of(page, size));
        return new ResponseEntity<>(userPage, HttpStatus.OK);

    }

    @GetMapping(value = "/user/withoutMemberRole/list")
    @PreAuthorize("hasAnyResource(@resourceItemService.MODEL_MEMBERS_MANAGE)")
    public ResponseEntity<?> listWithoutMemberRole(@RequestParam int page, @RequestParam int size
            , @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "false") Boolean filterOrgan, @RequestParam Long curModelVersion) {
        SearchDto userPage = userService.listWithoutMemberRole(filter, filterOrgan, PageRequest.of(page, size), curModelVersion);
        return new ResponseEntity<>(userPage, HttpStatus.OK);

    }

    //----------Save---------
    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveUser(@RequestBody User user) {
        User returnValue = userService.save(user);
        if (returnValue.getId() != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PutMapping("/user/profile")
    public ResponseEntity<?> updateCurrentUserHamsooProfile(@RequestPart("userDto") @Valid UserDto userDto,
                                                            @RequestPart(value = "multipartFile", required = false) MultipartFile multipartFile) {
        if (validateImages(multipartFile) && SecurityUtils.getUsernameOfCurrentUser().isPresent()) {
            User user = userService.updateHamsooAccountByUsername(SecurityUtils.getUsernameOfCurrentUser().get(), userDto, multipartFile);
            return new ResponseEntity<>(userService.mapToDto(user), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @GetMapping(value = "/user/profile")
    public ResponseEntity<?> getCurrentUserHamsooProfile() {
        if (SecurityUtils.getCurrentUser().isEmpty() && SecurityUtils.getUsernameOfCurrentUser().isPresent()) {
            User user = userService.getHamsooAccountByUsername(SecurityUtils.getUsernameOfCurrentUser().get());
            return new ResponseEntity<>(userService.mapToDto(user), HttpStatus.OK);
        }
        if (SecurityUtils.getCurrentAccount().isPresent())
            return new ResponseEntity<>(SecurityUtils.getCurrentAccount().get(), HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }

    @GetMapping("/user/profile/image")
    public ResponseEntity<?> getCurrentUserHamsooProfileImage() {
        FilesDto image = userService.getProfileImage(SecurityUtils.getCurrentUserOrElseThrow().getId());
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @PostMapping(value = "/user/profile/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> saveHamsooProfileImage(@Valid @RequestParam("multipartFile") MultipartFile multipartFile) {
        if (validateImages(multipartFile)) {
            Long imagId = userService.updateProfileImage(multipartFile, SecurityUtils.getCurrentUserOrElseThrow().getId());
            return new ResponseEntity<>(imagId, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }


    public Boolean validateImages(MultipartFile multipartFile) {
        BindException bindException = new BindException(multipartFile, "userDto");
        userImageValidator.validate(multipartFile, bindException);
        if (bindException.hasErrors()) {
            throw new ValidationException(bindException);
        }
        return true;
    }
}
