package com.youlan.common.excel.anno;

import cn.hutool.core.util.StrUtil;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelMappingProperty {
    /**
     * 映射注解集合
     */
    MappingProperty[] value();

    /**
     * 单元格前缀拼接字符
     */
    String prefixStr() default StrUtil.EMPTY;

    /**
     * 单元格后缀拼接字符
     */
    String suffixStr() default StrUtil.EMPTY;
}
