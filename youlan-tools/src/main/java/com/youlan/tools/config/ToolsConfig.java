package com.youlan.tools.config;

import com.youlan.common.core.spring.yaml.YamPropertySourceFactory;
import org.apache.velocity.app.Velocity;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
@PropertySource(value = {"classpath:tools.yml"}, factory = YamPropertySourceFactory.class)
@EnableConfigurationProperties({ToolsProperties.class})
public class ToolsConfig {

    @PostConstruct
    public void init() {
        initVelocity();
    }


    /**
     * 初始化模版引擎
     */
    public void initVelocity() {
        Properties properties = new Properties();
        properties.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        properties.setProperty(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8.displayName());
        Velocity.init(properties);
    }
}
