package com.youlan.system.config;

import com.youlan.common.captcha.config.ImageCaptchaProperties;
import com.youlan.common.captcha.config.SmsCaptchaProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "youlan.system")
public class SystemProperties {
    /**
     * 图形验证码配置
     */
    @NestedConfigurationProperty
    private ImageCaptchaProperties imageCaptcha;

    /**
     * 短信验证码配置
     */
    @NestedConfigurationProperty
    private SmsCaptchaProperties smsCaptcha;

    /**
     * 操作日志
     */
    @NestedConfigurationProperty
    private OperationLogProperties operationLog;
}
