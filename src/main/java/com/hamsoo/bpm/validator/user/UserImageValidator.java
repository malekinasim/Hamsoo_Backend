package com.hamsoo.bpm.validator.user;

import com.hamsoo.bpm.validator.HamsooValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

@Component
public class UserImageValidator extends HamsooValidator {
    @Value("${hamsoo.profile.image.multipart.max-size}")
    private String maxSize;
    @Value("${hamsoo.profile.image.multipart.file-type}")
    private String fileType;

    @Override
    public boolean supports(Class<?> clazz) {
        return MultipartFile.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        MultipartFile multipartFile = (MultipartFile) target;
        Assert.notNull(multipartFile, "fileType is null");
        if (multipartFile == null) {
            errors.rejectValue("contentType", "", "error.user.imageFileIsNull");
        }
        if (multipartFile.getSize() > Integer.parseInt(maxSize)) {
            errors.rejectValue("size", "", "error.user.imageFileSizeNotValid");
        }
        if (!Stream.of(fileType.toUpperCase().split(",")).anyMatch((type) -> multipartFile.getContentType().toUpperCase().equals(type))) {
            errors.rejectValue("contentType", "", "error.user.imageFileTypeNotValid");
        }
    }
}
