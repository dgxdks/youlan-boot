package com.youlan.plugin.sms.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.plugin.sms.dao.SmsDaoRedisImpl;
import lombok.AllArgsConstructor;
import org.dromara.sms4j.api.dao.SmsDao;
import org.dromara.sms4j.provider.config.SmsConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties({SmsProperties.class})
@AllArgsConstructor
public class SmsPluginConfig {
    private final SmsProperties smsProperties;

    @Bean
    public SmsDao smsDaoRedisImpl() {
        return new SmsDaoRedisImpl();
    }

    @Bean("customSmsConfig")
    @ConditionalOnClass(SmsConfig.class)
    public SmsConfig smsConfig(SmsConfig smsConfig) {
        // 合并配置项
        BeanUtil.copyProperties(smsProperties, smsConfig);
        return smsConfig;
    }
}
