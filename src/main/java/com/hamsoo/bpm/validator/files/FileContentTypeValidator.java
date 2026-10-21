package com.hamsoo.bpm.validator.files;

import com.hamsoo.bpm.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FileContentTypeValidator {

    public static final List<String> IMAGE_CONTENT_TYPE_WITHE_LIST = Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/svg+xml");
    public static final String HTML_CONTENT_TYPE_WITHE_LIST = "html/txt";
    public static final List<String> WORD_CONTENT_TYPE_WITHE_LIST =
            Arrays.asList("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    public static final List<String> EXCEL_CONTENT_TYPE_WITHE_LIST =
            Arrays.asList("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    public static final List<String> POWER_POINT_CONTENT_TYPE_WITHE_LIST =
            Arrays.asList("application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    public static final List<String> PDF_CONTENT_TYPE_WITHE_LIST = List.of("application/pdf");
    public static final List<String> TEXT_CONTENT_TYPE_WITHE_LIST = List.of("text/plain");
    public static final List<String> XML_CONTENT_TYPE_WITHE_LIST = List.of("application/xml", "text/xml");
    public static final List<String> USER_VALID_TYPES;

    static {
        List<String> userValidTypes = new ArrayList<>();
        userValidTypes.addAll(IMAGE_CONTENT_TYPE_WITHE_LIST);
        userValidTypes.addAll(WORD_CONTENT_TYPE_WITHE_LIST);
        userValidTypes.addAll(EXCEL_CONTENT_TYPE_WITHE_LIST);
        userValidTypes.addAll(POWER_POINT_CONTENT_TYPE_WITHE_LIST);
        userValidTypes.addAll(PDF_CONTENT_TYPE_WITHE_LIST);
        userValidTypes.addAll(TEXT_CONTENT_TYPE_WITHE_LIST);
        USER_VALID_TYPES = userValidTypes;
    }

    public void isImage(String contentType) {
        if (!StringUtils.hasText(contentType) ||
                !IMAGE_CONTENT_TYPE_WITHE_LIST.contains(contentType)) {
            throw new BusinessException("error.files.imageFormatNotValid");
        }
    }

    public void isHtml(String contentType) {
        if (!StringUtils.hasText(contentType) ||
                !HTML_CONTENT_TYPE_WITHE_LIST.contains(contentType)) {
            throw new BusinessException("error.files.fileFormatNotValid");
        }
    }

    public void isUserValidType(String contentType) {
        if (!StringUtils.hasText(contentType) ||
                !USER_VALID_TYPES.contains(contentType)) {
            throw new BusinessException("error.files.fileFormatNotValid");
        }
    }

    public void isXml(String contentType) {
        if (!StringUtils.hasText(contentType) ||
                !XML_CONTENT_TYPE_WITHE_LIST.contains(contentType)) {
            throw new BusinessException("error.files.xmlFormatNotValid");
        }
    }
}
