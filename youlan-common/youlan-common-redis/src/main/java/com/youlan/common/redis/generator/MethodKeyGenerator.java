package com.youlan.common.redis.generator;

import cn.hutool.core.text.StrPool;
import com.youlan.common.core.helper.AspectHelper;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

public class MethodKeyGenerator implements KeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        Method targetMethod = AspectHelper.getTargetMethod(joinPoint);
        Class<?> targetClass = AspectHelper.getTargetClass(joinPoint);
        return targetClass.getName() + StrPool.COLON + targetMethod.getName();
    }
}
