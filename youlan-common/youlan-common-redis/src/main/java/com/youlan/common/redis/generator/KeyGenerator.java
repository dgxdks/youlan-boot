package com.youlan.common.redis.generator;

import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public interface KeyGenerator {
    String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno);
}
