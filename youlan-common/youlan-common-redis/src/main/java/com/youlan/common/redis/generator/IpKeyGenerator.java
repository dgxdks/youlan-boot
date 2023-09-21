package com.youlan.common.redis.generator;

import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public class IpKeyGenerator implements KeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        return ServletHelper.getClientIp();
    }
}
