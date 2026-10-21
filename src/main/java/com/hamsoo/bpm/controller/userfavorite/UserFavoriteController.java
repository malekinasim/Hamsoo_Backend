package com.hamsoo.bpm.controller.userfavorite;

import com.hamsoo.bpm.model.dto.userfavorite.UserFavoriteDto;
import com.hamsoo.bpm.model.entity.userfavorite.UserFavorite;
import com.hamsoo.bpm.service.api.userfavorite.UserFavoriteService;
import com.hamsoo.bpm.validator.userfavorite.UserFavoriteValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;
    private final UserFavoriteValidator userFavoriteValidator;

    public UserFavoriteController(UserFavoriteService userFavoriteService,
                                  UserFavoriteValidator userFavoriteValidator) {
        this.userFavoriteService = userFavoriteService;
        this.userFavoriteValidator = userFavoriteValidator;
    }

    @InitBinder("userFavoriteDto")
    private void initResourceSchemaBinder(WebDataBinder binder) {
        binder.setValidator(userFavoriteValidator);
    }

    @PostMapping(value = "/user-favorites")
    public ResponseEntity<?> createUserFavorite(@Valid @RequestBody UserFavoriteDto userFavoriteDto) {
        UserFavorite userFavorite = userFavoriteService.create(userFavoriteDto);
        userFavoriteDto = userFavoriteService.mapToDto(userFavorite);
        return new ResponseEntity<>(userFavoriteDto, HttpStatus.OK);
    }

    @DeleteMapping("/user-favorites/{id}")
    public ResponseEntity<?> deleteUserFavorite(@PathVariable Long id) {
        userFavoriteService.delete(id, true);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
