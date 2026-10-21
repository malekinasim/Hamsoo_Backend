package com.hamsoo.bpm.common.utils;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final String DEFAULT_PREFIX = "{";
    private static final String DEFAULT_SUFFIX = "}";

    public static String applyParameter(String source, Map<String, String> param) {
        Assert.notNull(source, "source string is null");
        if (CollectionUtils.isEmpty(param)) {
            return source;
        }
        StringSubstitutor sub = new StringSubstitutor(param, DEFAULT_PREFIX, DEFAULT_SUFFIX);
        return sub.replace(source);
    }

//    public static List<String> getParameters(String source) {
//        Assert.notNull(source, "source string is null");
//        List<String> paramList = new ArrayList<>();
//        // Pattern p = Pattern.compile("(\\"+DEFAULT_PREFIX+")((.*?)+)(\\"+DEFAULT_SUFFIX+")");
//
//        Pattern p = Pattern.compile("(\\" + DEFAULT_PREFIX + ")(.*)(\\" + DEFAULT_SUFFIX + ")");
//
//        Arrays.stream(source.split("/")).forEach(
//                s ->
//                {
//                    Matcher matcher = p.matcher(s);
//                    String keys = "";
//                    String values = "";
//                    while (matcher.find())
//                        paramList.add(matcher.group(2));
//                }
//        );
//
//        return paramList;
//    }


    public static List<String> getParameters(String source) {
        Assert.notNull(source, "source string is null");
        List<String> paramList = new ArrayList<>();
        Pattern p = Pattern.compile("(\\"+DEFAULT_PREFIX+")((.*?)+)(\\"+DEFAULT_SUFFIX+")");
        //Pattern p = Pattern.compile("(\\"+DEFAULT_PREFIX+")(.*)(\\"+DEFAULT_SUFFIX+")");
        Matcher matcher = p.matcher(source);

        while (matcher.find())
            paramList.add(matcher.group(2));
        return paramList;
    }

    /**
     * replace arabic kaf - yeh - waw Letter
     * used decimal code of utf-8
     * more detail -> https://www.charset.org/utf-8/2
     */
    public static String replaceSpecialCharacter(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            long utf8DecimalCode = input.charAt(i);
            if (utf8DecimalCode == 1603) {
                chars[i] = Character.toString(1705).charAt(0);
            }
            if (utf8DecimalCode == 1574 || utf8DecimalCode == 1609 || utf8DecimalCode == 1610) {
                chars[i] = Character.toString(1740).charAt(0);
            }
            if (utf8DecimalCode == 1572) {
                chars[i] = Character.toString(1608).charAt(0);
            }
        }
        return new String(chars);
    }

}
