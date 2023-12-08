package com.youlan.plugin.pay.config;

import com.youlan.plugin.pay.factory.CachePayClientFactory;
import com.youlan.plugin.pay.factory.PayClientFactory;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({PayProperties.class})
public class PayConfig {

    @Bean
    @ConditionalOnMissingBean(PayClientFactory.class)
    public PayClientFactory payClientFactory() {
        return new CachePayClientFactory();
    }

}
