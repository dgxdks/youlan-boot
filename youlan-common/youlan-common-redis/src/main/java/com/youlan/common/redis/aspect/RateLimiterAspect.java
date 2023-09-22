package com.youlan.common.redis.aspect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.MessageHelper;
import com.youlan.common.redis.enums.LimitType;
import com.youlan.common.redis.generator.*;
import com.youlan.common.redis.limiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@Component
public class RateLimiterAspect {
    @Before("@annotation(rateRateLimiterAnno)")
    public void before(JoinPoint joinPoint, com.youlan.common.redis.anno.RateLimiter rateRateLimiterAnno) {
        //获取key生成器并生成key
        KeyGenerator keyGenerator = getKeyGenerator(joinPoint, rateRateLimiterAnno);
        String key = keyGenerator.generate(joinPoint, rateRateLimiterAnno);
        //拼接限流key
        List<String> rateLimitKeyItems = new ArrayList<>();
        rateLimitKeyItems.add(rateRateLimiterAnno.prefix());
        rateLimitKeyItems.add(key);
        //生成最终限流key
        String rateLimitKey = CollectionUtil.join(rateLimitKeyItems, StrPool.COLON);
        //生成限流器
        Class<? extends RateLimiter> limiterClass = rateRateLimiterAnno.limiter();
        RateLimiter rateLimiter = ReflectUtil.newInstance(limiterClass);
        //是否被限流处理逻辑
        boolean tryAcquire = rateLimiter.tryAcquire(rateLimitKey, joinPoint, rateRateLimiterAnno);
        if (tryAcquire) {
            return;
        }
        BizRuntimeException bizRuntimeException = new BizRuntimeException(rateRateLimiterAnno.errorCode());
        String errorMessage = rateRateLimiterAnno.errorMessage();
        //如果指定了错误信息则有可能要覆盖默认错误信息
        if (StrUtil.isNotBlank(errorMessage)) {
            String message = MessageHelper.message(errorMessage);
            //有信息才覆盖默认错误信息
            if (StrUtil.isNotBlank(message)) {
                bizRuntimeException.setErrorMsg(message);
            }
        }
        throw bizRuntimeException;
    }

    public KeyGenerator getKeyGenerator(JoinPoint joinPoint, com.youlan.common.redis.anno.RateLimiter rateRateLimiterAnno) {
        LimitType limitType = rateRateLimiterAnno.limitType();
        switch (limitType) {
            case CUSTOM_KEY:
                if (ObjectUtil.isNull(rateRateLimiterAnno.generator())) {
                    throw new BizRuntimeException("自定义key限流必须指定");
                }
                return ReflectUtil.newInstance(rateRateLimiterAnno.generator());
            case SP_EL_KEY:
                return new SpElKeyGenerator();
            case IP_REQUEST_URI:
                return new IpRequestUriKeyGenerator();
            case IP_METHOD:
                return new IpMethodKeyGenerator();
            case IP_METHOD_ARGS:
                return new IpMethodArgsUriKeyGenerator();
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
}
