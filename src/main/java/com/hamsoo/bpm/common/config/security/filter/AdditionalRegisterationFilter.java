package com.hamsoo.bpm.common.config.security.filter;

import com.hamsoo.bpm.common.config.security.tokenprovider.JwtTokenProvider;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.model.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;


@Order(Ordered.LOWEST_PRECEDENCE)
public class AdditionalRegisterationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String BEARER = "Bearer ";
    private String[] authWhitelist;

    @Value("${server.servlet.context-path}")
    private String contrextPath;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AdditionalRegisterationFilter(String[] authWhitelist) {
        this.authWhitelist = authWhitelist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String path = request.getRequestURI();
        if (Stream.of(authWhitelist).map(p -> (contrextPath + p).replace("**", "")).anyMatch(p -> path.startsWith(p))) {
            filterChain.doFilter(request, response);
        } else {
           String jwtToken= tokenProvider.getJwtToken(request);
            if (StringUtils.hasText(jwtToken)
                    && tokenProvider.validateAndParseToken(jwtToken)
            ) {
                Optional<UserDto> optionalUser = SecurityUtils.getCurrentUser();
                if (optionalUser.isPresent() && optionalUser.get().getComplated()) {
                    filterChain.doFilter(request, response);
                } else {
                    //response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "error.profile.notFull");
                    //filterChain.doFilter(request, response);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }


}
