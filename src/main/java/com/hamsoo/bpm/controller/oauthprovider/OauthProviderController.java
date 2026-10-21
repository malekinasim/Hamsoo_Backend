package com.hamsoo.bpm.controller.oauthprovider;

import com.hamsoo.bpm.model.dto.oauthprovider.OauthProviderDto;
import com.hamsoo.bpm.service.api.oauthprovider.OauthProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OauthProviderController {


    private final OauthProviderService oauthproviderService;

    public OauthProviderController(OauthProviderService oauthproviderService) {
        this.oauthproviderService = oauthproviderService;
    }


    //----------get All---------
    @GetMapping(value = "/public/oauthProvider/list")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<?>> listAllOauthProvider() {
        List<OauthProviderDto> oauthProviderPage = oauthproviderService.findAllProviderInfo();
        if (oauthProviderPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(oauthProviderPage, HttpStatus.OK);
        }
    }


}
