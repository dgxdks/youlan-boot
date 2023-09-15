package com.youlan.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "youlan.system")
public class SystemProperties {
    /**
     * 验证码配置
     */
    @NestedConfigurationProperty
    private CaptchaProperties captcha;

    /**
     * 存储配置
     */
    @NestedConfigurationProperty
    private StorageProperties storage;

    /**
     * 操作日志
     */
    @NestedConfigurationProperty
    private OperationLogProperties operationLog;
}
