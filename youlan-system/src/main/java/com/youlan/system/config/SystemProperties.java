package com.youlan.system.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "youlan.system")
public class SystemProperties {
    /**
     * 验证码配置
     */
    private CaptchaProperties captcha;

    /**
     * 存储配置
     */
    private StorageProperties storage;
}
