package com.youlan.common.captcha.helper;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import com.youlan.common.captcha.entity.CaptchaContext;

public class LineCaptchaHelper extends AbstractImageCaptchaHelper {

    @Override
    public AbstractCaptcha abstractCaptcha(CaptchaContext context) {
        int width = context.getWidth();
        int height = context.getHeight();
        int codeLength = context.getCodeLength();
        int shearNumber = context.getShearNumber();
        return CaptchaUtil.createLineCaptcha(width, height, codeLength, shearNumber);
    }
}
