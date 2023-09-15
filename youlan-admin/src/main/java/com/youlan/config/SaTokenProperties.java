package com.youlan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "youlan.admin.sa-token")
public class SaTokenProperties {
    /**
     * 要排除的路径
     */
    private List<String> excludePathPatterns = new ArrayList<>();
}
