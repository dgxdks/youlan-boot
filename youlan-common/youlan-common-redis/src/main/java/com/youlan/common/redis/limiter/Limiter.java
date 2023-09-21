package com.youlan.common.redis.limiter;

import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public interface Limiter {
    boolean limit(JoinPoint joinPoint, RateLimiter rateLimiterAnno);
}
