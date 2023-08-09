package com.youlan.controller.common;

import cn.dev33.satoken.annotation.SaIgnore;
import com.youlan.common.captcha.config.CaptchaProperties;
import com.youlan.common.captcha.entity.CaptchaContext;
import com.youlan.common.captcha.entity.ImageCaptcha;
import com.youlan.common.captcha.entity.SmsCaptcha;
import com.youlan.common.captcha.entity.dto.SmsCaptchaDTO;
import com.youlan.common.captcha.enums.CaptchaCodeType;
import com.youlan.common.captcha.enums.CaptchaType;
import com.youlan.common.captcha.helper.ImageCaptchaHelper;
import com.youlan.common.captcha.holder.CaptchaHelperHolder;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.crypto.anno.DecryptRequest;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.utils.SystemConfigUtil;
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
@RequestMapping("/captcha")
@AllArgsConstructor
public class CaptchaController extends BaseController {
    private CaptchaProperties captchaProperties;

    @SaIgnore
    @Operation(summary = "图形验证码")
    @PostMapping("/getImageCaptcha")
    public ApiResult getImageCaptcha() {
        boolean captchaEnabled = SystemConfigUtil.captchaImageEnabled();
        if (!captchaEnabled) {
            throw new BizRuntimeException("验证码功能已被禁用");
        }
        CaptchaType captchaType = captchaProperties.getCaptchaType();
        CaptchaCodeType captchaCodeType = captchaProperties.getCaptchaCodeType();
        int captchaCodeLen = captchaProperties.getCaptchaCodeLen();
        ImageCaptchaHelper captchaHelper = CaptchaHelperHolder.imageCaptchaHelper(captchaType);
        ImageCaptcha imageCaptcha = captchaHelper.createCaptcha(captchaCodeType, new CaptchaContext().setCodeLength(captchaCodeLen));
        return toSuccess(imageCaptcha);
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
