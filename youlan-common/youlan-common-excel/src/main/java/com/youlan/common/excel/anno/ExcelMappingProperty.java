package com.youlan.common.excel.anno;

import cn.hutool.core.text.StrPool;
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
     * 数据字典值名称分隔符(适用于多个字典值名称的情况)
     */
    String separator() default StrPool.COMMA;

    /**
     * 单元格前缀拼接字符
     */
    String prefixStr() default StrUtil.EMPTY;

    /**
     * 单元格后缀拼接字符
     */
    String suffixStr() default StrUtil.EMPTY;
}
