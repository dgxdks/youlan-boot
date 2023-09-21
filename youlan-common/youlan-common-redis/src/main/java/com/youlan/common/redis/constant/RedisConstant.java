package com.youlan.common.redis.constant;

public class RedisConstant {
    /**
     * 系统内置RedisTemplate名称{@link org.springframework.data.redis.core.RedisTemplate}
     */
    public static final String REDIS_TEMPLATE_BEAN_NAME = "jsonRedisTemplate";

    /**
     * 限流缓存前缀
     */
    public static final String REDIS_PREFIX_RATE_LIMIT = "rate_limit";

    public static final String COMMAND_STATS = "commandstats";
    public static final String DB_SIZE = "dbSize";
}
