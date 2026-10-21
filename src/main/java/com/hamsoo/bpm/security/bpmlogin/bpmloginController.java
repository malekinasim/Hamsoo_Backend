package com.hamsoo.bpm.security.bpmlogin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController

@PropertySource("classpath:application-security.properties")
public class bpmloginController {
    private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";
    private static String PROVIDER_PROPERTY_KEY = "spring.security.oauth2.client.provider.";


    @Autowired
    public  Environment environment ;


    @RequestMapping("/hamsoo")
    public void testController(HttpServletResponse response) throws IOException {
        response.sendRedirect(environment.getProperty("hamsoo.security.loginpage"));
    }

    @GetMapping("/callback/{registerId}")
    public void test(@PathVariable(name = "registerId")String  registerId,HttpServletResponse response) throws IOException {
        response.sendRedirect(environment.getProperty("hamsoo.security.loginpage"));
    }

    @GetMapping("/oidlogin/{registerId}")
    public ResponseEntity<?> getOIDLogin(@PathVariable(name = "registerId") String registerId){

        String atuUrl=environment.getProperty(PROVIDER_PROPERTY_KEY+registerId+".authorization-uri");
        if(atuUrl!=null && !atuUrl.isEmpty()) {
            String params = Arrays.asList(
                    "redirect_uri=" + environment.getProperty(CLIENT_PROPERTY_KEY + registerId + ".redirect-uri"),
                    "scope=" +environment.getProperty(CLIENT_PROPERTY_KEY + registerId + ".scope"),
                    "client_id=" + environment.getProperty(CLIENT_PROPERTY_KEY + registerId + ".client-id"),
                    "response_type=code"
            ).stream().collect(Collectors.joining("&"));
            return new ResponseEntity<>(atuUrl + "?" + params, HttpStatus.OK);
        }
         return new ResponseEntity<>( HttpStatus.EXPECTATION_FAILED);
    }
}
