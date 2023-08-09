package com.youlan.tools.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "youlan.tools")
public class ToolsProperties {
    private GeneratorProperties generator;
}
