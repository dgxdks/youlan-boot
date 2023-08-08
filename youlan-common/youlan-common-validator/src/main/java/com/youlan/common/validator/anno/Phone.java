package com.youlan.common.validator.anno;

import com.youlan.common.validator.validator.PhoneValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Constraint(validatedBy = {PhoneValidator.class})
public @interface Phone {
    String message() default "手机号码格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Type type() default Type.MOBILE;

    public enum Type {
        /**
         * 验证是否为手机号码（中国大陆）
         */
        MOBILE,
        /**
         * 验证是否为手机号码（中国香港）
         */
        MOBILE_HK,
        /**
         * 验证是否为手机号码（中国台湾
         */
        MOBILE_TW,
        /**
         * 验证是否为手机号码（中国澳门）
         */
        MOBILE_MO,
        /**
         * 验证是否为座机号码（中国大陆）
         */
        TEL,
        /**
         * 验证是否为座机号码（中国大陆）+ 400 + 800
         */
        TEL_400800,
        /**
         * 是否为座机号码+手机号码（中国大陆）+手机号码（中国香港）+手机号码（中国台湾）+手机号码（中国澳门）
         */
        PHONE
    }
}
