package com.hamsoo.bpm.common.config.filter;

import com.hamsoo.bpm.common.config.modificationrequest.ModificationRequestWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * this filter used for Modification request components(body, header, path and query params).
 * replace special character
 * todo handle xss attach
 */
@Component
public class ModificationRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ModificationRequestWrapper wrappedRequest = new ModificationRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrappedRequest, response);
    }

}
