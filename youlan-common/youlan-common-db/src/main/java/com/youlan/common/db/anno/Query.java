package com.youlan.common.db.anno;

import com.youlan.common.db.enums.QueryType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体类查询条件注解
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * 列名(不指定则默认为属性名,指定一个则代替属性名,指定多个则为或关系)
     */
    String[] column() default {};

    /**
     * 查询类型
     */
    QueryType type() default QueryType.EQUAL;

    /**
     * 是否允许查询条件是null(默认不允许)
     */
    boolean nullEnabled() default false;
}
