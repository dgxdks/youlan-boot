package com.youlan.framework.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FrameworkProperties.class})
@AllArgsConstructor
public class FrameworkConfig {

}
