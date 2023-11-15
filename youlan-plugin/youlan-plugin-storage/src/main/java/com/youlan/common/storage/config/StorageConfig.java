package com.youlan.common.storage.config;


import lombok.AllArgsConstructor;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFileStorage
@AllArgsConstructor
@EnableConfigurationProperties({StorageProperties.class})
public class StorageConfig {

}
