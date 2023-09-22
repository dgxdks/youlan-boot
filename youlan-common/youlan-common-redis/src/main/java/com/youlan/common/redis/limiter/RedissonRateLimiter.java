package com.youlan.common.redis.limiter;

import com.youlan.common.redis.helper.RedisHelper;
import org.aspectj.lang.JoinPoint;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;

import java.time.Duration;

public class RedissonRateLimiter implements RateLimiter {
    @Override
    public boolean tryAcquire(String rateLimitKey, JoinPoint joinPoint, com.youlan.common.redis.anno.RateLimiter rateRateLimiterAnno) {
        //获取限流操作对象
        RRateLimiter rateLimiter = RedisHelper.getRateLimiter(rateLimitKey);
        RateType rateType = rateRateLimiterAnno.rateType();
        long rate = rateRateLimiterAnno.rate();
        long interval = rateRateLimiterAnno.interval();
        long timeToAlive = rateRateLimiterAnno.timeToAlive();
        boolean trySetRate = rateLimiter.trySetRate(rateType, rate, interval, RateIntervalUnit.SECONDS);
        //如果设置成功则说明是第一次设置需要设置过期时间
        if (trySetRate) {
            rateLimiter.expire(Duration.ofSeconds(timeToAlive));
        }
        boolean tryAcquire = rateLimiter.tryAcquire();
        System.out.println(tryAcquire);
        return tryAcquire;
    }
}
