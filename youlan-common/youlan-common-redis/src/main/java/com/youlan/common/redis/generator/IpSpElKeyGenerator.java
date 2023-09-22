package com.youlan.common.redis.generator;

import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

public class IpSpElKeyGenerator implements KeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        SpElKeyGenerator spElKeyGenerator = new SpElKeyGenerator();
        String spElKey = spElKeyGenerator.generate(joinPoint, rateLimiterAnno);
        String clientIp = ServletHelper.getClientIp();
        return concat(clientIp, spElKey);
    }
}
