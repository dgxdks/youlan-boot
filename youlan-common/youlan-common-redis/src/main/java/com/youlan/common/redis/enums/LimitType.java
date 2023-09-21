package com.youlan.common.redis.enums;

public enum LimitType {
    /**
     * 自定义key限流
     */
    CUSTOM,

    /**
     * 指定唯一key限流
     */
    KEY,

    /**
     * IP限流
     */
    IP,

    /**
     * IP+请求URI限流
     */
    IP_REQUEST_URI,

    /**
     * IP+方法名称限流
     */
    IP_METHOD,

    /**
     * IP+方法名称+方法参数限流
     */
    IP_METHOD_ARGS,

    /**
     * 方法名称限流(Class+Method)
     */
    METHOD,

    /**
     * 方法名称+参数限流(Class+Method+Args)
     */
    METHOD_ARGS,

    /**
     * 请求URI限流
     */
    REQUEST_URI;
}
