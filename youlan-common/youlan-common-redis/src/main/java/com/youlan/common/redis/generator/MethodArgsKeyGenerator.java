package com.youlan.common.redis.generator;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.helper.AspectHelper;
import com.youlan.common.redis.anno.RateLimiter;
import org.aspectj.lang.JoinPoint;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MethodArgsKeyGenerator extends MethodKeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        String methodKey = super.generate(joinPoint, rateLimiterAnno);
        Object[] targetMethodArgs = AspectHelper.getTargetMethodArgs(joinPoint);
        if (ArrayUtil.isEmpty(targetMethodArgs)) {
            return methodKey;
        }
        String argsKey = Arrays.stream(targetMethodArgs)
                .map(ObjectUtil::toString)
                .collect(Collectors.joining(StrPool.COMMA));
        return methodKey + StrPool.COLON + argsKey;
    }
}
