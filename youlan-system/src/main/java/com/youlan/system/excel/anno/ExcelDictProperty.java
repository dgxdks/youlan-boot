package com.youlan.system.excel.anno;

import cn.hutool.core.util.StrUtil;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDictProperty {
    /**
     * 数据字典typeKey
     */
    String value();

    /**
     * 单元格前缀拼接字符
     */
    String prefixStr() default StrUtil.EMPTY;

    /**
     * 单元格后缀拼接字符
     */
    String suffixStr() default StrUtil.EMPTY;
}
