package com.youlan.common.validator.helper;

import cn.hutool.core.collection.CollectionUtil;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidatorHelper {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> Set<ConstraintViolation<T>> validate(T t) {
        return validator.validate(t);
    }

    public static <T> void validateWithThrow(T t) {
        Set<ConstraintViolation<T>> constraintViolations = validate(t);
        if (CollectionUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    public static <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... groups) {
        return validator.validate(t, groups);
    }

    public static <T> void validateWithThrow(T t, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolations = validate(t, groups);
        if (CollectionUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    public Validator validator() {
        return validator;
    }
}
