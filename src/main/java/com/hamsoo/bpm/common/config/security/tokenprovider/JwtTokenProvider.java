package com.hamsoo.bpm.common.config.security.tokenprovider;


import com.hamsoo.bpm.common.config.security.filter.HamsooSwitchUserFilter;
import com.hamsoo.bpm.common.config.security.filter.JWTAuthenticationFilter;
import com.hamsoo.bpm.common.config.security.model.dto.hamsoouserdetail.Token;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${hamsoo.jwt.expiration}")
    private Long expiration;


    @Value("${hamsoo.curernt.organ.cookiename}")
    private String curOrgCookieName;

    @Value("${hamsoo.jwt.expiration.accesstoken.cookiename}")
    private String accessTokenCookieName;


    @Value("${hamsoo.jwt.secret}")
    private String secret;

    public Token issueToken(Map<String, Object> claims) {
        Assert.notNull(claims, "claims is null");
        Date now = new Date(System.currentTimeMillis());
        Long duration = now.getTime() + expiration;
        Date expirDate = new Date(duration);
        return new Token(Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirDate)
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact(), expiration,
                LocalDateTime.ofInstant(expirDate.toInstant(), ZoneId.systemDefault()));
    }

    public Token issueToken(Authentication authentication) {
        Assert.notNull(authentication, "authentication is null");
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", ((UserDetails) authentication.getPrincipal()).getUsername());
        // claims.put("principal", authentication.getPrincipal());


        List<GrantedAuthority> authoritieList = new ArrayList<>();
        //TODO
        //dont use app role authority , organ role authority and resource authority in token generation
        // authoritieList.addAll(((UserDetails) authentication.getPrincipal()).getAuthorities());

        //just add switch user authority in token
        authoritieList.addAll(authentication.getAuthorities().stream()
                .filter(auth -> auth instanceof SwitchUserGrantedAuthority)
                .map(auth -> {
                            SwitchUserGrantedAuthority sauth = (SwitchUserGrantedAuthority) auth;
                            String username = ((UserDetails) sauth.getSource().getPrincipal()).getUsername();
                            String password = ((UserDetails) sauth.getSource().getPrincipal()).getPassword();
                          //  Collection<? extends GrantedAuthority> authorities = ((UserDetails) sauth.getSource().getPrincipal()).getAuthorities();
                            UserDetails userDetails = new User(username, password,new ArrayList<>());
                            SwitchUserGrantedAuthority newSauth = new SwitchUserGrantedAuthority(
                                    sauth.getAuthority(),
                                    new UsernamePasswordAuthenticationToken(userDetails, "", new ArrayList<>())
                            );
                            return newSauth;
                        }
                ).collect(Collectors.toList())
        );

        claims.put("authorities", authoritieList);
        Date now = new Date(System.currentTimeMillis());
        Long duration = now.getTime() + expiration;
        Date expireDate = new Date(duration);
        return new Token(Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact(), expiration,
                LocalDateTime.ofInstant(expireDate.toInstant(), ZoneId.systemDefault()));

    }


    public Claims parseTokenAndGetClaims(String jwtToken) {
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(jwtToken).getBody();
    }


    public boolean validateAndParseToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(jwtToken).getBody();
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            throw ex;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(JWTAuthenticationFilter.AUTHORIZATION_HEADER_KEY);
        if (StringUtils.hasText(bearerToken) &&
                bearerToken.startsWith(JWTAuthenticationFilter.BEARER)) {
            String accessToken = bearerToken.substring(7);
            if (accessToken == null) return null;

            return accessToken;
        }
        return null;
    }

    public String getJwtToken(HttpServletRequest request) {
        try {
            String token = getJwtFromRequest(request);
            return token;

        } catch (Exception e) {
            System.out.println("exception" + e);
        }
        return null;

    }

    public String getCurOrganFromRequest(HttpServletRequest request) {
        String curOrgan = request.getHeader(curOrgCookieName);
        if (StringUtils.hasText(curOrgan)) {
            return curOrgan;
        }
        return null;
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final UserDetails userDetails) {
        final Claims claims = parseTokenAndGetClaims(token);
        List<GrantedAuthority> authoritieList = new ArrayList<>();
         if(claims.get("authorities")!=null) {
             authoritieList.addAll(((ArrayList) (claims.get("authorities"))).stream()
                     .filter(lh ->
                             ((LinkedHashMap) lh).get("authority")
                                     .equals(HamsooSwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR)
                     ).map(lh -> {

                                 String authority = (String) ((LinkedHashMap) lh).get("authority");
                                 LinkedHashMap source = (LinkedHashMap) ((LinkedHashMap) lh).get("source");
                                 LinkedHashMap principal = (LinkedHashMap) source.get("principal");
                                 String username = (String) principal.get("username");
                                 String password = (String) principal.get("password");
                                 List<SwitchUserGrantedAuthority> authorities = ((ArrayList) source.get("authorities")).stream().map(auth ->
                                         new SimpleGrantedAuthority((String) ((LinkedHashMap) auth)
                                                 .get("authority"))
                                 ).toList();
                                 UserDetails orignaluserDetails = new User(username, password, authorities);
                                 SwitchUserGrantedAuthority newSauth = new SwitchUserGrantedAuthority(
                                         authority,
                                         new UsernamePasswordAuthenticationToken(orignaluserDetails, "", authorities)
                                 );
                                 return newSauth;
                             }
                     ).toList());
         }
        if(!CollectionUtils.isEmpty(userDetails.getAuthorities()))
           authoritieList.addAll(userDetails.getAuthorities());
         return new UsernamePasswordAuthenticationToken(userDetails, "", authoritieList);
    }
}
