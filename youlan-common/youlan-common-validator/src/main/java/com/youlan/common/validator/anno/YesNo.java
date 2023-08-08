package com.youlan.common.validator.anno;

import com.youlan.common.validator.validator.YesNoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Constraint(validatedBy = {YesNoValidator.class})
public @interface YesNo {
    String message() default "是否值不允许";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 拓展可允许的状态值
     */
    String[] extend() default {};
}
