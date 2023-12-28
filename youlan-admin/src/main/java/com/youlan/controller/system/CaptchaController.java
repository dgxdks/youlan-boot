package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import com.youlan.common.captcha.config.ImageCaptchaProperties;
import com.youlan.common.captcha.config.SmsCaptchaProperties;
import com.youlan.common.captcha.entity.ImageCaptcha;
import com.youlan.common.captcha.entity.SmsCaptcha;
import com.youlan.common.captcha.entity.dto.SmsCaptchaDTO;
import com.youlan.common.captcha.enums.CodeType;
import com.youlan.common.captcha.helper.CaptchaHelper;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.redis.anno.RateLimiter;
import com.youlan.controller.base.BaseController;
import com.youlan.plugin.sms.entity.SmsRecord;
import com.youlan.plugin.sms.service.biz.SmsBizService;
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
    private final SmsBizService smsBizService;

    @SaIgnore
    @Operation(summary = "图形验证码")
    @PostMapping("/getImageCaptcha")
    public ApiResult getImageCaptcha() {
        ImageCaptchaProperties imageCaptchaProperties = systemProperties.getImageCaptcha();
        ImageCaptcha imageCaptcha = CaptchaHelper.createImageCaptcha(imageCaptchaProperties);
        imageCaptcha.setCaptchaEnabled(SystemConfigHelper.captchaImageEnabled());
        return toSuccess(imageCaptcha.setCaptchaEnabled(SystemConfigHelper.captchaImageEnabled()));
    }

    @SaIgnore
    @RateLimiter(spElKey = "#{'system:captcha:' + #dto.mobile}", rate = 1, interval = 60, errorMessage = "验证码请求频繁")
    @Operation(summary = "短信验证码")
    @PostMapping("/getSmsCaptcha")
    public ApiResult getSmsCaptcha(@Validated @RequestBody SmsCaptchaDTO dto) {
        SmsCaptchaProperties smsCaptchaProperties = systemProperties.getSmsCaptcha();
        int codeTimeout = smsCaptchaProperties.getCodeTimeout();
        CodeType codeType = smsCaptchaProperties.getCodeType();
        int codeLength = smsCaptchaProperties.getCodeLength();
        SmsCaptcha smsCaptcha = CaptchaHelper.createSmsCaptcha(codeTimeout, codeType, codeLength);
        String captchaCode = smsCaptcha.getCaptchaCode();
        SmsRecord smsRecord = smsBizService.sendMessage("system-captcha", dto.getMobile(), captchaCode);
        smsRecord.validateSendSuccess();
        return toSuccess(smsCaptcha);
    }
}
