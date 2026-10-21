package com.hamsoo.bpm.controller.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/public/status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
