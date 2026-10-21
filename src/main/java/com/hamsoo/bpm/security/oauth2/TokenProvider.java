package com.hamsoo.bpm.security.oauth2;


import com.sun.security.auth.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;


import java.util.Date;

public class TokenProvider {
    Environment environment;

    public TokenProvider(Environment environment) {
        this.environment = environment;
    }

    public String createToken(Authentication authentication) {
        
        return null;
    }
}
