package com.hamsoo.bpm.common.config.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.common.exception.ErrorDetails;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.user.UserDto;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

;

@Component
public class UnAuthorizedHandler implements AuthenticationEntryPoint {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String token = tokenProvider.getJwtToken(request);
        try {
            if (token == null || !tokenProvider.validateAndParseToken(token)) {

                returnResponse(ResourceBundleUtils.getMessage("error.user.isNotAuthenticated"), response, request,HttpServletResponse.SC_FORBIDDEN);

            } else {
                Optional<UserDto> optionalUser = SecurityUtils.getCurrentUser();
                if (!optionalUser.isPresent() || !optionalUser.get().getComplated()) {
                    returnResponse(ResourceBundleUtils.getMessage("error.profile.notFull"), response, request,HttpServletResponse.SC_FORBIDDEN);
                } else {
                    returnResponse(authException.getMessage(), response, request,HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        } catch (ExpiredJwtException ex) {
            returnResponse(ResourceBundleUtils.getMessage("error.token.Expired"), response, request,HttpServletResponse.SC_UNAUTHORIZED);
        }

    }

    private void returnResponse(String message,HttpServletResponse response,HttpServletRequest request,int status) throws IOException {
        List<String> errors = Collections.singletonList(message);
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), request.getRequestURI(),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        mapper.writeValue(responseStream, errorDetails);
        responseStream.flush();
    }
}
