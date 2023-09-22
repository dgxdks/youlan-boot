package com.youlan.common.redis.generator;

import cn.hutool.core.text.StrPool;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public interface KeyGenerator {
    String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno);

    default String concat(String... keys) {
        return String.join(StrPool.COLON, keys);
    }
}
