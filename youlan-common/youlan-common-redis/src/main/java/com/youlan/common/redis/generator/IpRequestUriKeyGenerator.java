package com.youlan.common.redis.generator;

import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public class IpRequestUriKeyGenerator implements KeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        RequestUriKeyGenerator requestUriKeyGenerator = new RequestUriKeyGenerator();
        String requestUriKey = requestUriKeyGenerator.generate(joinPoint, rateLimiterAnno);
        String clientIp = ServletHelper.getClientIp();
        return concat(clientIp, requestUriKey);
    }
}
