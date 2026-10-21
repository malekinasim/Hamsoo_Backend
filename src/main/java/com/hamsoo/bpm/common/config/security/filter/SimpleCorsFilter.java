package com.hamsoo.bpm.common.config.security.filter;

import org.springframework.core.env.Environment;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
public class SimpleCorsFilter implements Filter {
    Environment environment;

    public SimpleCorsFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        response.setHeader("Access-Control-Allow-Origin", this.environment.getProperty("cors.allowedOrigins"));
        response.setHeader("Access-Control-Allow-Methods", this.environment.getProperty("cors.allowedMethods"));
        response.setHeader("Access-Control-Allow-Headers", this.environment.getProperty("cors.allowedHeaders"));
        response.setHeader("Access-Control-Max-Age", this.environment.getProperty("cors.maxAge"));
        response.setHeader("X-Frame-Options", "ALLOW-FROM *");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Content-Security-Policy", "frame-ancestors 'self' *");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(200);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }


}
