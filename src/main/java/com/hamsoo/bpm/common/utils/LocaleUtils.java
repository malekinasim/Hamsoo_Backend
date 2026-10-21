package com.hamsoo.bpm.common.utils;

import com.hamsoo.bpm.common.exception.ServiceException;
import org.springframework.core.env.Environment;

import java.util.Locale;

public class LocaleUtils {

    public static Locale getInstance() {
        String locale = SpringContext.getBean(Environment.class).getProperty("hamsoo.locale");
        if (locale == null) {
            throw new ServiceException("error.localeUtils.localeNotSetInHamsooConfig");
        }
        return new Locale(locale);
    }
}
