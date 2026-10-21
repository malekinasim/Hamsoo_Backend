package com.hamsoo.bpm.common.config.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class CustomHttpServletRequest extends HttpServletRequestWrapper {
    Map<String, Object> customHeaderMap;

    public CustomHttpServletRequest(HttpServletRequest request) {
        super(request);
        customHeaderMap = new HashMap();
    }

    public void addHeader(String name, String value) {
        customHeaderMap.put(name, value);
    }

    @Override
    public String getParameter(String name) {
        String paramValue = (String) customHeaderMap.get(name);  // query Strings
        if (paramValue == null) {
            paramValue = super.getParameter(name);
        }
        return paramValue;
    }
}
