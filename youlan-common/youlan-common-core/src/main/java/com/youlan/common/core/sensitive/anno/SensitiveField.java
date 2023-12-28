package com.youlan.common.core.sensitive.anno;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.youlan.common.core.sensitive.enums.SensitiveType;
import com.youlan.common.core.sensitive.jackson.SensitiveFiledJsonSerializer;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveFiledJsonSerializer.class)
public @interface SensitiveField {

    /**
     * 脱敏类型
     */
    SensitiveType type();

    /**
     * 左侧需要保留明文的长度
     */
    int prefixNoMaskLen() default 0;

    /**
     * 右侧需要保留明文的长度
     */
    int suffixNoMaskLen() default 0;

    /**
     * 自定义脱敏字符
     */
    String maskStr() default "*";
}
