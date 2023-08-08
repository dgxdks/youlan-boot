package com.youlan.common.excel.anno;

import cn.hutool.core.util.StrUtil;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MappingProperty {
    /**
     * 映射值
     */
    String value();

    /**
     * 映射名称
     */
    String name();

    /**
     * 单元格前缀拼接字符
     */
    String prefixStr() default StrUtil.EMPTY;

    /**
     * 单元格后缀拼接字符
     */
    String suffixStr() default StrUtil.EMPTY;
}
