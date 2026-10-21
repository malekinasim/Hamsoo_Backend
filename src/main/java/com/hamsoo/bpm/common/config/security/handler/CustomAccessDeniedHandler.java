package com.hamsoo.bpm.common.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hamsoo.bpm.common.exception.ErrorDetails;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import com.hamsoo.bpm.service.api.resourceitem.ResourceItemService;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    private ApplicationContext applicationContext;
    private ResourceItemService resourceItemService;

    public CustomAccessDeniedHandler(ApplicationContext applicationContext,
                                     ResourceItemService resourceItemService) {

        this.applicationContext = applicationContext;
        this.resourceItemService = resourceItemService;

    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        PreAuthorize preAuthorize = null;
        try {
            preAuthorize = getMethodPreAuthorizeInfo(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ErrorDetails errorDetails;
        if (preAuthorize != null) {
            String[] splitMethod = preAuthorize.value().split("\\(");
            if (splitMethod.length >= 1) {
                List<String> resourceNames = Arrays.stream(
                                splitMethod[1].split(","))
                        .filter(str -> str.startsWith("@resourceItemService."))
                        .map(str -> {
                            String res = str.substring(str.indexOf(".") + 1);
                            if (res.endsWith(")"))
                                return res.substring(0, res.length() - 1);
                            return res;

                        })
                        .toList();

                if (resourceNames != null) {
                    errorDetails = resourceItemService.getResourceItemsAccessDeniedMsg(resourceNames);

                } else {
                    List<String> errors = new ArrayList<>();
                    errors.add(ResourceBundleUtils.getMessage("error.AccessDenied.DefaultMsg"));
                    errorDetails = new ErrorDetails(errors, LocalDateTime.now(), null,
                            SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());

                }
            } else {
                List<String> errors = new ArrayList<>();
                errors.add(ResourceBundleUtils.getMessage("error.AccessDenied.DefaultMsg"));
                errorDetails = new ErrorDetails(errors, LocalDateTime.now(), null,
                        SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());

            }
        } else {
            List<String> errors = new ArrayList<>();
            errors.add(ResourceBundleUtils.getMessage("error.AccessDenied.DefaultMsg"));
            errorDetails = new ErrorDetails(errors, LocalDateTime.now(), null,
                    SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());

        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        mapper.writeValue(responseStream, errorDetails);
        responseStream.flush();
    }

    private PreAuthorize getMethodPreAuthorizeInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestMappingHandlerMapping req2HandlerMapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
        HandlerExecutionChain handlerExeChain = req2HandlerMapping.getHandler(request);
        HandlerMethod handlerMethod = (HandlerMethod) handlerExeChain.getHandler();
//        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap =
//                req2HandlerMapping.getHandlerMethods();

//        for (Map.Entry<RequestMappingInfo,HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
//            if(RequestMappingInfo.paths(request.getRequestURI()).equals(infoEntry.getKey())) {
//                HandlerMethod handlerMethod = infoEntry.getValue();
        if (handlerMethod != null) {
            PreAuthorize methodAnnotation = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            return methodAnnotation;
        }
//            }
//        }

//        if (!CollectionUtils.isEmpty(anonUrls)) {
//            urlRegistry.antMatchers(anonUrls.toArray(new String[0])).permitAll();
//        }
//        if (!response.isCommitted()) {
//            List<HandlerMapping> handlerMappings = dispatcherServlet.getHandlerMappings();
//            if (handlerMappings != null) {
//                HandlerExecutionChain handler = null;
//                for (HandlerMapping handlerMapping : handlerMappings) {
//                    try {
//                        handler = handlerMapping.getHandler(request);
//                    } catch (Exception e) {
//                    }
//                    if (handler != null)
//                        break;
//                }
//                if (handler != null && handler.getHandler() instanceof HandlerMethod) {
//                    HandlerMethod method = (HandlerMethod) handler.getHandler();
//                    PreAuthorize methodAnnotation = method.getMethodAnnotation(PreAuthorize.class);
//                    return methodAnnotation;
//                }
//            }
        //  }
        return null;
    }

}
