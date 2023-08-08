package com.youlan.common.validator.anno;

import com.youlan.common.validator.validator.StrInValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Constraint(validatedBy = {StrInValidator.class})
public @interface StrIn {
    /**
     * 指定可匹配的字符,为空时会跳过校验
     */
    String[] value() default {};

    String message() default "当前值不在指定范围内";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
