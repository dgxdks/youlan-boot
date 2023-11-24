package com.youlan.plugin.pay.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PayProperties.class})
public class PayConfig {
}
