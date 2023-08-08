package com.youlan.tools.config;

import com.youlan.tools.utils.VelocityUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ToolsConfig {
    @PostConstruct
    public void init() {
        VelocityUtil.initVelocity();
    }
}
