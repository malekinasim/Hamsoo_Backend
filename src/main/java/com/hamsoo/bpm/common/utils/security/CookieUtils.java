package com.hamsoo.bpm.common.utils.security;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

public class CookieUtils {
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";


    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            if (Stream.of(cookies).anyMatch(cookie -> cookie.getName().equals(name))) {
                return Stream.of(cookies).filter(cookie -> cookie.getName().equals(name)).findFirst();

            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, Boolean isSecure, Boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(isSecure);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static ResponseCookie createCookie(String name, String value, Long maxAge, Boolean isSecure) {
        return ResponseCookie.from(name, value)
                .maxAge(Duration.ofSeconds(maxAge))
                .sameSite("None")
                .httpOnly(false)
                .secure(isSecure)
                .path("/")
                .build();
    }

    public static HttpCookie createCookie(String name, String value, String path, Long maxAge) {
        return ResponseCookie.from(name, value)
                .maxAge(Duration.ofSeconds(maxAge))
                .sameSite("None")
                .httpOnly(true)
                // .secure(true)
                .path(path)
                .build();
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static HttpCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .maxAge(0)
                .httpOnly(true)
                .path("/")
                .build();
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
