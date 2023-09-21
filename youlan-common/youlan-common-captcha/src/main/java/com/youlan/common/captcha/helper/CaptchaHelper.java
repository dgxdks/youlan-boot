package com.youlan.common.captcha.helper;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.captcha.config.ImageCaptchaProperties;
import com.youlan.common.captcha.entity.ImageCaptcha;
import com.youlan.common.captcha.entity.SmsCaptcha;
import com.youlan.common.captcha.enums.CodeType;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.redis.helper.RedisHelper;

import java.time.Duration;

import static com.youlan.common.captcha.constant.CaptchaConstant.REDIS_PREFIX_CAPTCHA_DEFAULT;

public class CaptchaHelper {

    /**
     * 创建图形验证码
     *
     * @param properties 图形验证码配置
     * @return 图形验证码
     */
    public static ImageCaptcha createImageCaptcha(ImageCaptchaProperties properties) {
        Assert.notNull(properties, () -> new BizRuntimeException("必须指定图形验证码配置上下文"));
        int width = properties.getWidth();
        int height = properties.getHeight();
        int codeLength = properties.getCodeLength();
        int shearNumber = properties.getShearNumber();
        CodeType codeType = properties.getCodeType();
        CodeGenerator codeGenerator = createCodeGenerator(codeType, properties.getCodeLength());
        AbstractCaptcha abstractCaptcha = CaptchaUtil.createLineCaptcha(width, height, codeLength, shearNumber);
        abstractCaptcha.setGenerator(codeGenerator);
        //设置验证码背景颜色
        abstractCaptcha.setBackground(properties.getColor());
        //设置验证码字体
        abstractCaptcha.setFont(properties.getFont());
        abstractCaptcha.createCode();
        String captchaCode = abstractCaptcha.getCode();
        //兼容四则运算产生的验证码
        captchaCode = String.valueOf((int) Calculator.conversion(captchaCode));
        String captchaId = IdUtil.fastUUID();
        int codeTimeout = properties.getCodeTimeout();
        createStringCaptcha(captchaId, captchaCode, codeTimeout);
        return new ImageCaptcha()
                .setCaptchaId(captchaId)
                .setCodeTimeout(codeTimeout)
                .setCaptchaImg(abstractCaptcha.getImageBase64());
    }

    /**
     * 创建短信验证码
     *
     * @param codeTimeout 验证码超时时间
     * @param codeLength  验证码长度
     */

    public static SmsCaptcha createSmsCaptcha(int codeTimeout, int codeLength) {
        return createSmsCaptcha(codeTimeout, CodeType.NUMBER, codeLength);
    }

    /**
     * 创建短信验证码
     *
     * @param codeTimeout 验证码超时时间
     * @param codeType    验证码类型
     * @param codeLength  验证码长度
     */
    public static SmsCaptcha createSmsCaptcha(int codeTimeout, CodeType codeType, int codeLength) {
        CodeGenerator codeGenerator = createCodeGenerator(codeType, codeLength);
        String captchaCode = codeGenerator.generate();
        String captchaId = IdUtil.fastUUID();
        createStringCaptcha(captchaId, captchaCode, codeTimeout);
        return new SmsCaptcha()
                .setCaptchaId(captchaId)
                .setCodeTimeout(codeTimeout);
    }

    /**
     * 创建验证码
     *
     * @param captchaId   验证码ID
     * @param captchaCode 验证码
     * @param codeTimeout 验证码超时时间
     */
    public static void createStringCaptcha(String captchaId, String captchaCode, int codeTimeout) {
        Assert.notBlank(captchaId, () -> new BizRuntimeException("必须指定全局唯一的验证码ID"));
        Assert.notBlank(captchaCode, () -> new BizRuntimeException("生成验证码前必须指定验证码"));
        Assert.isTrue(codeTimeout > 0, () -> new BizRuntimeException("验证码过期时间必须大于0"));
        String codeRedisKey = getCaptchaRedisKey(captchaId);
        RedisHelper.setCacheObject(codeRedisKey, captchaCode, Duration.ofSeconds(codeTimeout));
    }


    /**
     * 匹配验证码
     *
     * @param captchaId   验证码ID
     * @param captchaCode 验证码
     * @return 校验是否通过
     */
    public static boolean match(String captchaId, String captchaCode) {
        String storageCaptchaCode = RedisHelper.getCacheObject(getCaptchaRedisKey(captchaId));
        if (StrUtil.isBlank(storageCaptchaCode)) {
            throw new BizRuntimeException(ApiResultCode.A0008);
        }
        return StrUtil.equalsAnyIgnoreCase(storageCaptchaCode, captchaCode);
    }

    /**
     * 校验验证码
     *
     * @param captchaId   验证码ID
     * @param captchaCode 验证码
     */
    public static void check(String captchaId, String captchaCode) {
        Assert.isTrue(match(captchaId, captchaCode), ApiResultCode.A0007::getException);
    }

    /**
     * @param codeType   验证码类型
     * @param codeLength 验证码长度
     * @return 验证码生成器
     */
    public static CodeGenerator createCodeGenerator(CodeType codeType, int codeLength) {
        Assert.notNull(codeType, "必须指定验证码类型");
        Assert.isTrue(codeLength > 0, "验证码长度必须指定");
        switch (codeType) {
            case CHAR:
                return new RandomGenerator(RandomUtil.BASE_CHAR, codeLength);
            case CHAR_NUMBER:
                return new RandomGenerator(RandomUtil.BASE_CHAR_NUMBER, codeLength);
            case MATH:
                // 强制指定参与运算的数字位数为1，不然用户没个计算器搞不好算不出来
                return new MathGenerator(1);
            case NUMBER:
            default:
                return new RandomGenerator(RandomUtil.BASE_NUMBER, codeLength);
        }
    }

    /**
     * 获取验证码缓存键名
     */
    private static String getCaptchaRedisKey(String captchaId) {
        return REDIS_PREFIX_CAPTCHA_DEFAULT + captchaId;
    }
}
