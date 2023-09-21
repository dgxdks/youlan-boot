package com.youlan.common.redis.generator;

import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.AspectHelper;
import com.youlan.common.core.helper.SpElHelper;
import com.youlan.common.redis.anno.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Slf4j
public class SpElKeyGenerator implements KeyGenerator {
    @Override
    public String generate(JoinPoint joinPoint, RateLimiter rateLimiterAnno) {
        String key = rateLimiterAnno.key();
        if (!SpElHelper.isExpressionString(key)) {
            return key;
        }
        try {
            String expressionKey = rateLimiterAnno.key();
            Object[] args = AspectHelper.getMethodArgs(joinPoint);
            String[] names = AspectHelper.getMethodNames(joinPoint);
            StandardEvaluationContext evaluationContext = SpElHelper.createEvaluationContext(names, args);
            Expression expression = SpElHelper.parseExpression(expressionKey);
            return expression.getValue(evaluationContext, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizRuntimeException(e);
        }
    }
}
