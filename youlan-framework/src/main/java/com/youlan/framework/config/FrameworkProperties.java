package com.youlan.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "youlan.framework")
public class FrameworkProperties {
    /**
     * 操作日志
     */
    @NestedConfigurationProperty
    private OperationLogProperties operationLog;

    /**
     * sa-token配置
     */
    @NestedConfigurationProperty
    private SaTokenProperties saToken;
}
