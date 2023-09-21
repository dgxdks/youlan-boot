package com.youlan.common.redis.limiter;

import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public class DefaultLimiter extends AbstractLimiter {
    @Override
    public boolean limit(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        return false;
    }
}
