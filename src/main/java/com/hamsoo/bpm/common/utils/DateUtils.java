package com.hamsoo.bpm.common.utils;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String currentDateTimePersianInString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss", LocaleUtils.getInstance());
        return df.format(new Date());
    }

    public static String currentDateTimePersianInString(String pattern, String numberFormatLocale) {
        SimpleDateFormat df = new SimpleDateFormat(pattern, LocaleUtils.getInstance());
        df.setNumberFormat(NumberFormat.getNumberInstance(new Locale(numberFormatLocale)));
        return df.format(new Date());
    }
    public static String getDateTimePersianInString(LocalDateTime localDateTime, String pattern, String numberFormatLocale) {
        SimpleDateFormat df = new SimpleDateFormat(pattern, LocaleUtils.getInstance());
        df.setNumberFormat(NumberFormat.getNumberInstance(new Locale(numberFormatLocale)));
        return df.format(localDateTime);
    }
}
