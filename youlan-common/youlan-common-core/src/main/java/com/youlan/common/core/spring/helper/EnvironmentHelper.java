package com.youlan.common.core.spring.helper;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;

public class EnvironmentHelper {

    public static String resolvePlaceHolders(String text) {
        return getEnvironment().resolvePlaceholders(text);
    }

    public static Environment getEnvironment() {
        return SpringUtil.getBean(Environment.class);
    }
}
