package com.youlan.system.helper;

import cn.hutool.extra.spring.SpringUtil;
import com.youlan.system.service.ConfigService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.youlan.system.constant.SystemConstant.*;

@Component
@AllArgsConstructor
public class SystemConfigHelper {
    private static final ConfigService configService = SpringUtil.getBean(ConfigService.class);

    /**
     * 用户登录日志
     */
    public static boolean loginLogEnabled() {
        return configService.getConfigValueBoolean(CONFIG_KEY_LOGIN_LOG_ENABLED);
    }

    /**
     * 用户初始密码
     */
    public static String userInitPassword() {
        return configService.getConfigValueString(CONFIG_KEY_USER_INIT_PASSWORD);
    }

    /**
     * 是否开启图片验证码
     */
    public static boolean captchaImageEnabled() {
        return configService.getConfigValueBoolean(CONFIG_KEY_CAPTCHA_IMAGE_ENABLED);
    }

    /**
     * 是否允许用户注册
     */
    public static boolean accountRegistryEnabled() {
        return configService.getConfigValueBoolean(CONFIG_KEY_LOGIN_ACCOUNT_REGISTRY_ENABLED);
    }

    /**
     * 用户登录最大重试次数
     */
    public static int loginMaxRetryTimes() {
        Number loginRetryTimes = configService.getConfigValueNumber(CONFIG_KEY_LOGIN_RETRY_TIMES);
        return loginRetryTimes.intValue();
    }

    /**
     * 用户登录锁定时间(秒)
     */
    public static int loginLockTime() {
        Number loginLockTime = configService.getConfigValueNumber(CONFIG_KEY_LOGIN_LOCK_TIME);
        return loginLockTime.intValue();
    }

    /**
     * 用户登录重试策略
     */
    public static String loginRetryStrategy() {
        return configService.getConfigValueString(CONFIG_KEY_LOGIN_RETRY_STRATEGY);
    }
}
