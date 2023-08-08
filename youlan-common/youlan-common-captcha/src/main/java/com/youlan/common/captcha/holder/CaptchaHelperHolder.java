package com.youlan.common.captcha.holder;

import cn.hutool.extra.spring.SpringUtil;
import com.youlan.common.captcha.enums.CaptchaType;
import com.youlan.common.captcha.helper.*;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Import(SpringUtil.class)
@Component
public class CaptchaHelperHolder {

    public static ImageCaptchaHelper imageCaptchaHelper(CaptchaType captchaType) {
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
        return SpringUtil.getBean(CaptchaHelper.class);
    }

    public static CircleCaptchaHelper circleCaptchaHelper() {
        return SpringUtil.getBean(CircleCaptchaHelper.class);
    }

    public static LineCaptchaHelper lineCaptchaHelper() {
        return SpringUtil.getBean(LineCaptchaHelper.class);
    }

    public static ShearCaptchaHelper shearCaptchaHelper() {
        return SpringUtil.getBean(ShearCaptchaHelper.class);
    }

    public static ImageCaptchaHelper imageCaptchaHelper() {
        return SpringUtil.getBean(ImageCaptchaHelper.class);
    }

    public static SmsCaptchaHelper smsCaptchaHelper() {
        return SpringUtil.getBean(SmsCaptchaHelper.class);
    }
}
