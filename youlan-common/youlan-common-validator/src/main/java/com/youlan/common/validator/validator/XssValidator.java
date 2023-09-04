package com.youlan.common.validator.validator;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.youlan.common.validator.anno.Xss;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class XssValidator implements ConstraintValidator<Xss, String> {
    @Override
    public void initialize(Xss constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isBlank(value)) {
            return true;
        }
        return !ReUtil.contains(HtmlUtil.RE_HTML_MARK, value);
    }
}
