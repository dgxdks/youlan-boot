package com.youlan.common.captcha.helper;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.youlan.common.captcha.entity.CaptchaContext;
import com.youlan.common.captcha.entity.CaptchaInfo;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.redis.helper.RedisHelper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class CaptchaHelper {
    public static final String REDIS_PREFIX_CAPTCHA = "captcha:";

    /**
     * 生成包含sourceId的限流redisKey
     */
    public static String createLimitRedisKey(String sourceId) {
        return REDIS_PREFIX_CAPTCHA + sourceId;
    }

    /**
     * 生成包含captchaId的redisKey
     */
    public static String createCodeRedisKey(String captchaId) {
        return REDIS_PREFIX_CAPTCHA + captchaId;
    }

    /**
     * 创建验证码(支持限流)
     *
     * @param captchaCode 验证码
     * @param sourceId    请求验证码的来源ID(通过此ID可对验证码需求方进行限流,例如传入用户手机号、用户ID)
     * @param codeTimeout 验证码超时时间
     * @param codeLimit   超时时间内最大请求次数
     */
    public CaptchaInfo createCaptcha(String captchaCode, String sourceId, long codeTimeout, int codeLimit) {
        assert ObjectUtil.isAllNotEmpty(captchaCode, sourceId, codeTimeout, codeLimit);
        String captchaId = createCaptchaId();
        String limitRedisKey = createLimitRedisKey(sourceId);
        Integer count = RedisHelper.get(limitRedisKey);
        //不存在则生成初始值
        if (ObjectUtil.isNull(count)) {
            count = 1;
            RedisHelper.set(limitRedisKey, count, codeTimeout, TimeUnit.SECONDS);
        }
        //超过最大限制则直接终端执行
        if (count > codeLimit) {
            throw new BizRuntimeException(ApiResultCode.A0005);
        }
        String codeRedisKey = createCodeRedisKey(captchaId);
        //保存验证码及生成次数信息
        RedisHelper.set(codeRedisKey, captchaCode, codeTimeout, TimeUnit.SECONDS);
        RedisHelper.increment(limitRedisKey, 1);
        return new CaptchaInfo().setCaptchaId(captchaId)
                .setCaptchaCode(captchaCode)
                .setCodeTimeout(codeTimeout);
    }

    /**
     * 创建验证码
     *
     * @param captchaCode 验证码
     * @param codeTimeout 验证码超时时间
     */
    public CaptchaInfo createCaptcha(String captchaCode, long codeTimeout) {
        assert ObjectUtil.isAllNotEmpty(captchaCode, codeTimeout);
        String captchaId = createCaptchaId();
        String codeRedisKey = createCodeRedisKey(captchaId);
        RedisHelper.set(codeRedisKey, captchaCode, codeTimeout, TimeUnit.SECONDS);
        return new CaptchaInfo()
                .setCaptchaId(captchaId)
                .setCaptchaCode(captchaCode)
                .setCodeTimeout(codeTimeout);
    }

    /**
     * 创建验证码
     *
     * @param context 验证码配置上下文
     * @return 验证码信息
     */
    public CaptchaInfo createCaptcha(CaptchaContext context) {
        String captchaCode = context.getCaptchaCode();
        if (StrUtil.isBlank(captchaCode)) {
            throw new BizRuntimeException("生成验证码前必须指定验证码");
        }
        String sourceId = context.getSourceId();
        CaptchaInfo captchaInfo;
        if (StrUtil.isNotBlank(sourceId) && ObjectUtil.isNotNull(context.getCodeLimit())) {
            captchaInfo = createCaptcha(captchaCode, context.getSourceId(), context.getCodeTimeout(), context.getCodeLimit());
        } else {
            captchaInfo = createCaptcha(captchaCode, context.getCodeTimeout());
        }
        return captchaInfo;
    }

    /**
     * 校验验证码
     *
     * @param captchaId   生成验证码时返回的验证码ID
     * @param captchaCode 需要校验的验证码
     */
    public static boolean verifyCaptcha(String captchaId, String captchaCode) {
        String codeRedisKey = createCodeRedisKey(captchaId);
        Optional<String> captchaCodeOpt = RedisHelper.getOpt(codeRedisKey);
        RedisHelper.delete(codeRedisKey);
        if (captchaCodeOpt.isEmpty()) {
            throw new BizRuntimeException(ApiResultCode.A0008);
        }
        return captchaCodeOpt.get().equals(captchaCode);
    }

    /**
     * 生成验证码唯一ID
     */
    public static String createCaptchaId() {
        return IdUtil.fastUUID();
    }
}
