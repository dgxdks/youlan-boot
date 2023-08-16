package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import com.youlan.common.captcha.entity.CaptchaContext;
import com.youlan.common.captcha.entity.ImageCaptcha;
import com.youlan.common.captcha.entity.SmsCaptcha;
import com.youlan.common.captcha.entity.dto.SmsCaptchaDTO;
import com.youlan.common.captcha.enums.CaptchaCodeType;
import com.youlan.common.captcha.enums.CaptchaType;
import com.youlan.common.captcha.helper.AbstractImageCaptchaHelper;
import com.youlan.common.captcha.holder.CaptchaHelperHolder;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.crypto.anno.DecryptRequest;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.config.CaptchaProperties;
import com.youlan.system.config.SystemProperties;
import com.youlan.system.helper.SystemConfigHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "验证码管理")
@RestController
@RequestMapping("/system/captcha")
@AllArgsConstructor
public class CaptchaController extends BaseController {
    private final SystemProperties systemProperties;

    @SaIgnore
    @Operation(summary = "图形验证码")
    @PostMapping("/getImageCaptcha")
    public ApiResult getImageCaptcha() {
        CaptchaProperties captchaProperties = systemProperties.getCaptcha();
        CaptchaType captchaType = captchaProperties.getCaptchaType();
        CaptchaCodeType captchaCodeType = captchaProperties.getCaptchaCodeType();
        int captchaCodeLen = captchaProperties.getCaptchaCodeLen();
        AbstractImageCaptchaHelper captchaHelper = CaptchaHelperHolder.imageCaptchaHelper(captchaType);
        ImageCaptcha imageCaptcha = captchaHelper.createCaptcha(captchaCodeType, new CaptchaContext().setCodeLength(captchaCodeLen));
        return toSuccess(imageCaptcha.setCaptchaEnabled(SystemConfigHelper.captchaImageEnabled()));
    }

    @SaIgnore
    @DecryptRequest
    @Operation(summary = "短信验证码")
    @PostMapping("/getSmsCaptcha")
    public ApiResult getSmsCaptcha(@Validated @RequestBody SmsCaptchaDTO dto) {
        SmsCaptcha smsCaptcha = CaptchaHelperHolder.smsCaptchaHelper().createSmsCaptcha("1888888888");
        return toSuccess(smsCaptcha);
    }
}
