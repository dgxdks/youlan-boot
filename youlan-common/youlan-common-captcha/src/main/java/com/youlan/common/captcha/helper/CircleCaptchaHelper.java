package com.youlan.common.captcha.helper;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import com.youlan.common.captcha.entity.CaptchaContext;
import org.springframework.stereotype.Component;

@Component
public class CircleCaptchaHelper extends ImageCaptchaHelper {

    @Override
    public AbstractCaptcha abstractCaptcha(CaptchaContext context) {
        int width = context.getWidth();
        int height = context.getHeight();
        int codeLength = context.getCodeLength();
        int circleNumber = context.getCircleNumber();
        return CaptchaUtil.createCircleCaptcha(width, height, codeLength, circleNumber);
    }
}
