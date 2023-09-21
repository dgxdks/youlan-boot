package com.youlan.common.core.helper;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import org.springframework.core.env.Environment;

public class EnvironmentHelper {
    @Getter
    private static final Environment environment = SpringUtil.getBean(Environment.class);

    public static String resolvePlaceHolders(String text) {
        return getEnvironment().resolvePlaceholders(text);
    }

}
