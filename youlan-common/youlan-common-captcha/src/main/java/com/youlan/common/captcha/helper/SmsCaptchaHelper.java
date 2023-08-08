package com.youlan.common.captcha.helper;

import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.youlan.common.captcha.entity.CaptchaContext;
import com.youlan.common.captcha.entity.CaptchaInfo;
import com.youlan.common.captcha.entity.SmsCaptcha;
import org.springframework.stereotype.Component;

@Component
public class SmsCaptchaHelper extends CaptchaHelper {
    public static final int SMS_CAPTCHA_CODE_LIMIT_DEFAULT = 1;

    /**
     * 生成短信验证码
     *
     * @param mobile 手机号
     */
    public SmsCaptcha createSmsCaptcha(String mobile) {
        return createSmsCaptcha(mobile, new CaptchaContext());
    }

    /**
     * 生成短信验证码
     *
     * @param mobile  手机号
     * @param context 验证码配置上下文
     */
    public SmsCaptcha createSmsCaptcha(String mobile, CaptchaContext context) {
        if (ObjectUtil.isNull(context.getCodeLimit())) {
            context.setCodeLimit(SMS_CAPTCHA_CODE_LIMIT_DEFAULT);
        }
        context.setSourceId(mobile);
        return createSmsCaptcha(context);
    }

    /**
     * 生成短信验证码
     *
     * @param mobile    手机号
     * @param codeLimit 超时时间内最大生成次数
     * @param context   验证码配置上下文
     */
    public SmsCaptcha createSmsCaptcha(String mobile, int codeLimit, CaptchaContext context) {
        context.setSourceId(mobile)
                .setCodeLimit(codeLimit);
        return createSmsCaptcha(context);
    }

    /**
     * 生成短信验证码
     *
     * @param mobile    手机号
     * @param codeLimit 超时时间内最大生成次数
     */
    public SmsCaptcha createSmsCaptcha(String mobile, int codeLimit) {
        CaptchaContext context = new CaptchaContext()
                .setSourceId(mobile)
                .setCodeLimit(codeLimit);
        return createSmsCaptcha(context);
    }

    /**
     * 生成短信验证码
     *
     * @param context 验证码配置上下文
     */
    public SmsCaptcha createSmsCaptcha(CaptchaContext context) {
        RandomGenerator randomGenerator = new RandomGenerator(RandomUtil.BASE_NUMBER, context.getCodeLength());
        String captchaCode = randomGenerator.generate();
        context.setCaptchaCode(captchaCode);
        Integer codeLimit = context.getCodeLimit();
        long codeTimeout = context.getCodeTimeout();
        CaptchaInfo captchaInfo = createCaptcha(captchaCode, context.getSourceId(), codeTimeout, codeLimit);
        return new SmsCaptcha()
                .setMobile(context.getSourceId())
                .setCodeTimeout(captchaInfo.getCodeTimeout())
                .setCaptchaId(captchaInfo.getCaptchaId());
    }

}
