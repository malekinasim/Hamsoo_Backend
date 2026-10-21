package com.hamsoo.bpm.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Set;

public abstract class HamsooValidator implements Validator {

    @Override
    public void validate(Object target, Errors errors) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        javax.validation.Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(target);
        violations.forEach(p -> errors.rejectValue(p.getPropertyPath().toString(), "", p.getMessage()));
    }

}
