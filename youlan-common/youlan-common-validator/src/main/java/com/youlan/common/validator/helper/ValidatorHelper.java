package com.youlan.common.validator.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidatorHelper {

    private static Validator validator;

    static {
        try {
            Validator validator = SpringUtil.getBean(Validator.class);
            ValidatorHelper.validator = ObjectUtil.isNull(validator) ? createValidator() : validator;
        } catch (Exception ignore) {
            ValidatorHelper.validator = createValidator();
        }
    }

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

    public static Validator createValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
