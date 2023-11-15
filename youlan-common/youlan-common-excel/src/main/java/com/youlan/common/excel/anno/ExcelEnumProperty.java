package com.youlan.common.excel.anno;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelEnumProperty {
    /**
     * 枚举集合
     */
    Class<? extends Enum<?>> value();

    /**
     * 数据字典值名称分隔符(适用于多个字典值名称的情况)
     */
    String separator() default StrPool.COMMA;

    /**
     * 枚举值映射字段
     */
    String codeField() default "code";

    /**
     * 枚举名称映射字段
     */
    String textField() default "text";

    /**
     * 单元格前缀拼接字符
     */
    String prefixStr() default StrUtil.EMPTY;

    /**
     * 单元格后缀拼接字符
     */
    String suffixStr() default StrUtil.EMPTY;
}
