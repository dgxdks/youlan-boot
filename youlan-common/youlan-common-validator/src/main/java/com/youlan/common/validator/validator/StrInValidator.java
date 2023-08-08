package com.youlan.common.validator.validator;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.validator.anno.StrIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StrInValidator implements ConstraintValidator<StrIn, String> {
    private StrIn strIn;

    @Override
    public void initialize(StrIn constraintAnnotation) {
        this.strIn = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isBlank(value)) {
            return true;
        }
        //未指定值时放开校验
        if (ArrayUtil.isEmpty(strIn.value())) {
            return true;
        }
        return ArrayUtil.contains(strIn.value(), value);
    }
}
