package com.youlan.common.core.translation.anno;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.youlan.common.core.translation.MappingTranslationHandler;
import com.youlan.common.core.translation.TranslationHandler;
import com.youlan.common.core.translation.jackson.TranslationFieldJsonSerializer;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = TranslationFieldJsonSerializer.class)
public @interface TranslationField {
    /**
     * 转换控制器
     */
    Class<? extends TranslationHandler> value() default MappingTranslationHandler.class;

    /**
     * 指定从当前对象中的某个字段值进行转换,不指定则默认为当前注解字段
     */
    String translateFrom() default StrUtil.EMPTY;

    /**
     * KV对类型转换
     */
}
