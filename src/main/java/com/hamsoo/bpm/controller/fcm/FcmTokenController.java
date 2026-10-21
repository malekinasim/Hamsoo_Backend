package com.hamsoo.bpm.controller.fcm;

import com.hamsoo.bpm.model.dto.fcm.FcmTokenDto;
import com.hamsoo.bpm.model.entity.fcm.FcmToken;
import com.hamsoo.bpm.service.api.fcm.FcmTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    public FcmTokenController(FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    @PostMapping("/fcm-tokens/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody FcmTokenDto fcmTokenDto) {
        FcmToken fcmToken = fcmTokenService.subscribe(fcmTokenDto);
        fcmTokenDto = fcmTokenService.mapToDto(fcmToken);
        return new ResponseEntity<>(fcmTokenDto, HttpStatus.OK);
    }

    @PostMapping("/fcm-tokens/unsubscribe")
    public ResponseEntity<?> unsubscribe(@Valid @RequestBody FcmTokenDto fcmTokenDto) {
        fcmTokenService.unsubscribe(fcmTokenDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
