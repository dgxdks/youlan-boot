package com.youlan.common.redis.aspect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.AspectHelper;
import com.youlan.common.core.helper.SpElHelper;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.redis.anno.RateLimiter;
import com.youlan.common.redis.enums.LimitType;
import com.youlan.common.redis.generator.*;
import com.youlan.common.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.youlan.common.redis.constant.RedisConstant.REDIS_PREFIX_RATE_LIMIT;

@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    //    @Before("@annotation(rateLimiterAnno)")
//    public void before(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
//        String rateLimitKey = getRateLimitKey(joinPoint, rateLimiterAnno);
//        long rate = rateLimiterAnno.rate();
//        long interval = rateLimiterAnno.interval();
//        TimeUnit timeUnit = rateLimiterAnno.timeUnit();
//        LimitType strategy = rateLimiterAnno.strategy();
//        RateType rateType = strategy == LimitType.CLIENT ? RateType.PER_CLIENT : RateType.OVERALL;
//        long availablePermits = RedisHelper.rateLimiterAndGet(rateLimitKey, rateType, rate, interval, timeUnit);
//        System.out.println(availablePermits);
//    }
    @Before("@annotation(rateLimiterAnno)")
    public void before(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        String prefix = rateLimiterAnno.prefix();
        long rate = rateLimiterAnno.rate();
        long interval = rateLimiterAnno.interval();
        RateIntervalUnit unit = rateLimiterAnno.unit();
        RateType rateType = rateLimiterAnno.rateType();
        List<String> rateLimitKeyItems = new ArrayList<>();
        //拼接前缀
        if (StrUtil.isNotBlank(prefix)) {
            rateLimitKeyItems.add(prefix);
        }
        //拼接key
        KeyGenerator keyGenerator = routeKeyGenerator(joinPoint, rateLimiterAnno);
        String key = keyGenerator.generate(joinPoint, rateLimiterAnno);
        rateLimitKeyItems.add(key);
        //生成最终限流key
        String rateLimitKey = CollectionUtil.join(rateLimitKeyItems, StrPool.COLON);
        //获取限流操作对象
        RRateLimiter rateLimiter = RedisHelper.getRateLimiter(rateLimitKey);
    }

    public KeyGenerator routeKeyGenerator(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        LimitType limitType = rateLimiterAnno.limitType();
        switch (limitType) {
            case CUSTOM:
                if (ObjectUtil.isNull(rateLimiterAnno.generator())) {
                    throw new BizRuntimeException("自定义key限流必须指定");
                }
                return ReflectUtil.newInstance(rateLimiterAnno.generator());
            case KEY:
                return new SpElKeyGenerator();
            case IP:
                return new IpKeyGenerator();
            case IP_REQUEST_URI:
                break;
            case IP_METHOD:
                break;
            case IP_METHOD_ARGS:
                break;
            case METHOD:
                return new MethodKeyGenerator();
            case METHOD_ARGS:
                return new MethodArgsKeyGenerator();
            case REQUEST_URI:
                return new RequestUriKeyGenerator();

            default:
                throw new BizRuntimeException("限流类型不支持");
        }
    }

    public String getRateLimitKey(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        LimitType strategy = rateLimiterAnno.strategy();
        String key = rateLimiterAnno.key();

    }
}
