package com.youlan.system.config;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.youlan.system.org.interceptor.OrgModelInnerInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@Import(SpringUtil.class)
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(SystemProperties.class)
public class SystemConfig {
    private final MybatisPlusInterceptor mybatisPlusInterceptor;

    @PostConstruct
    public void init() {
        mybatisPlusInterceptor.addInnerInterceptor(new OrgModelInnerInterceptor());
    }
}
