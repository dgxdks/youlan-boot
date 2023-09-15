package com.youlan.system.anno;

import com.youlan.system.constant.OperationLogType;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    /**
     * 日志名称
     */
    String name();

    /**
     * 日志类型
     */
    String type() default OperationLogType.OPERATION_LOG_TYPE_OTHER;

    /**
     * 是否开启
     */
    boolean enabled() default true;

    /**
     * 是否保存查询参数
     */
    boolean saveQuery() default true;

    /**
     * 是否保存body参数
     */
    boolean saveBody() default true;

    /**
     * 是否保存response参数
     */
    boolean saveResponse() default true;
}