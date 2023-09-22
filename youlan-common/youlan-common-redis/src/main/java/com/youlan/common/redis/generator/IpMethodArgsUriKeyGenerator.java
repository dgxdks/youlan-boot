package com.youlan.common.redis.generator;

import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public class IpMethodArgsUriKeyGenerator implements KeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        MethodArgsKeyGenerator methodArgsKeyGenerator = new MethodArgsKeyGenerator();
        String methodArgsKey = methodArgsKeyGenerator.generate(joinPoint, rateLimiterAnno);
        String clientIp = ServletHelper.getClientIp();
        return concat(clientIp, methodArgsKey);
    }
}
