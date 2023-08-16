package com.youlan.common.captcha.holder;

import cn.hutool.extra.spring.SpringUtil;
import com.youlan.common.captcha.enums.CaptchaType;
import com.youlan.common.captcha.helper.*;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Import(SpringUtil.class)
@Component
public class CaptchaHelperHolder {
    private static final CircleCaptchaHelper circleCaptchaHelper = new CircleCaptchaHelper();
    private static final LineCaptchaHelper lineCaptchaHelper = new LineCaptchaHelper();
    private static final ShearCaptchaHelper shearCaptchaHelper = new ShearCaptchaHelper();
    private static final SmsCaptchaHelper smsCaptchaHelper = new SmsCaptchaHelper();
    private static final CaptchaHelper captchaHelper = new CaptchaHelper();

    public static AbstractImageCaptchaHelper imageCaptchaHelper(CaptchaType captchaType) {
        switch (captchaType) {
            case CIRCLE:
                return circleCaptchaHelper();
            case SHEAR:
                return shearCaptchaHelper();
            case SMS:
            case LINE:
            default:
                return lineCaptchaHelper();
        }
    }

    public static CaptchaHelper captchaHelper() {
        return new CaptchaHelper();
    }

    public static CircleCaptchaHelper circleCaptchaHelper() {
        return circleCaptchaHelper;
    }

    public static LineCaptchaHelper lineCaptchaHelper() {
        return lineCaptchaHelper;
    }

    public static ShearCaptchaHelper shearCaptchaHelper() {
        return shearCaptchaHelper;
    }

    public static SmsCaptchaHelper smsCaptchaHelper() {
        return smsCaptchaHelper;
    }
}
