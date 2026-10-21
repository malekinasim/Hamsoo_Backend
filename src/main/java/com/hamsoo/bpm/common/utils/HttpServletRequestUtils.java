package com.hamsoo.bpm.common.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

public class HttpServletRequestUtils {

    public static String getCharacterEncoding(HttpServletRequest request) {
        return StringUtils.hasText(request.getCharacterEncoding()) ?
                request.getCharacterEncoding() : Charset.defaultCharset().name();
    }

}
