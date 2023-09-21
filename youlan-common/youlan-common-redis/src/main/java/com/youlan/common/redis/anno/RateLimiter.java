package com.youlan.common.redis.anno;

import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.redis.constant.RedisConstant;
import com.youlan.common.redis.enums.LimitType;
import com.youlan.common.redis.generator.KeyGenerator;
import com.youlan.common.redis.limiter.DefaultLimiter;
import com.youlan.common.redis.limiter.Limiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流key前缀
     */
    String prefix() default RedisConstant.REDIS_PREFIX_RATE_LIMIT;

    /**
     * 限流key
     * 支持EL表达式，仅{@link LimitType#KEY}有效
     */
    String key() default StrUtil.EMPTY;

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.KEY;

    /**
     * 限流key生成器
     * 仅{@link LimitType#CUSTOM}有效
     */
    Class<? extends KeyGenerator> generator() default KeyGenerator.class;

    /**
     * 限流执行器
     */
    Class<? extends Limiter> limiter() default DefaultLimiter.class;

    /**
     * 限流速率类型
     */
    RateType rateType() default RateType.OVERALL;

    /**
     * 限流速率
     */
    long rate();

    /**
     * 限流间隔时间
     */
    long interval();

    /**
     * 限流间隔时间单位
     */
    RateIntervalUnit unit() default RateIntervalUnit.SECONDS;

    /**
     * 触发限流后的响应信息
     * 支持国际化，如果指定则会替代{@link #errorCode()}中的异常消息
     */
    String errorMessage() default StrUtil.EMPTY;

    /**
     * 触发限流后的响应码
     */
    ApiResultCode errorCode() default ApiResultCode.B0019;
}
