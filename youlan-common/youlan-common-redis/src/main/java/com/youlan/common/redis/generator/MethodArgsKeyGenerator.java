package com.youlan.common.redis.generator;

import com.youlan.common.core.helper.AspectHelper;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public class MethodArgsKeyGenerator implements KeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        return AspectHelper.getMethod(joinPoint).getName();
    }
}
