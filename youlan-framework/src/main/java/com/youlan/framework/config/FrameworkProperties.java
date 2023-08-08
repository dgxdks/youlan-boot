package com.youlan.framework.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "youlan.framework")
public class FrameworkProperties {
    /**
     * 操作日志
     */
    private SystemLogProperties systemLog;

    /**
     * sa-token配置
     */
    private SaTokenProperties saToken;
}
