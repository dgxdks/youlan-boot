package com.youlan.tools.config;

import com.youlan.common.core.spring.yaml.YamPropertySourceFactory;
import com.youlan.tools.utils.VelocityUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource(value = {"classpath:tools.yml"}, factory = YamPropertySourceFactory.class)
@EnableConfigurationProperties({ToolsProperties.class})
public class ToolsConfig {
    @PostConstruct
    public void init() {
        VelocityUtil.initVelocity();
    }
}
