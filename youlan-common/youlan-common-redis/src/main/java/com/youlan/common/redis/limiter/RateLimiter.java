package com.youlan.common.redis.limiter;

import org.aspectj.lang.JoinPoint;

public interface RateLimiter {
    boolean tryAcquire(String rateLimitKey, JoinPoint joinPoint, com.youlan.common.redis.anno.RateLimiter rateLimiterAnno);
}
