package com.youlan.tools.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GeneratorProperties.class})
@AllArgsConstructor
public class ToolsProperties {
    private final GeneratorProperties generatorProperties;
}
