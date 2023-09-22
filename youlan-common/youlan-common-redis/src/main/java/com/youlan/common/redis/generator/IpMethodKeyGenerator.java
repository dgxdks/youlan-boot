package com.youlan.common.redis.generator;

import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public class IpMethodKeyGenerator implements KeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        MethodKeyGenerator methodKeyGenerator = new MethodKeyGenerator();
        String methodKey = methodKeyGenerator.generate(joinPoint, rateLimiterAnno);
        String clientIp = ServletHelper.getClientIp();
        return concat(clientIp, methodKey);
    }
}
