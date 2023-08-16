package com.youlan.system.helper;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.system.entity.Config;
import com.youlan.system.service.biz.ConfigBizService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.youlan.system.constant.SystemConstant.*;

@Component
@AllArgsConstructor
public class SystemConfigHelper {
    private static final ConfigBizService CONFIG_BIZ_SERVICE = SpringUtil.getBean(ConfigBizService.class);

    /**
     * 是否开启图片验证码
     */
    public static boolean captchaImageEnabled() {
        return getConfigValueBoolean(CONFIG_KEY_CAPTCHA_IMAGE_ENABLED);
    }

    /**
     * 用户登录最大重试次数
     */
    public static int loginMaxRetryTimes() {
        Number loginRetryTimes = getConfigValueNumber(CONFIG_KEY_LOGIN_RETRY_TIMES);
        return loginRetryTimes.intValue();
    }

    /**
     * 用户登录锁定时间(秒)
     */
    public static int loginLockTime() {
        Number loginLockTime = getConfigValueNumber(CONFIG_KEY_LOGIN_LOCK_TIME);
        return loginLockTime.intValue();
    }

    /**
     * 用户登录重试策略
     */
    public static String loginRetryStrategy() {
        return getConfigValueString(CONFIG_KEY_LOGIN_RETRY_STRATEGY);
    }


    /**
     * 获取boolean类型配置值
     */
    public static boolean getConfigValueBoolean(String configKey) {
        Config config = CONFIG_BIZ_SERVICE.getConfigByConfigKeyIfExist(configKey);
        String configValue = config.getConfigValue();
        Boolean configValueBoolean = Convert.toBool(configValue, null);
        if (ObjectUtil.isNull(configValueBoolean)) {
            throw new BizRuntimeException(StrUtil.format("获取配置键值为[{}]的布尔配置值失败", configKey));
        }
        return configValueBoolean;
    }

    /**
     * 获取数字类型配置值
     */
    public static Number getConfigValueNumber(String configKey) {
        Config config = CONFIG_BIZ_SERVICE.getConfigByConfigKeyIfExist(configKey);
        String configValue = config.getConfigValue();
        Number configValueNumber = Convert.toNumber(configValue, null);
        if (ObjectUtil.isNull(configValueNumber)) {
            throw new BizRuntimeException(StrUtil.format("获取配置键值为[{}]的数值配置值失败", configKey));
        }
        return configValueNumber;
    }

    /**
     * 获取字符类型配置值
     */
    public static String getConfigValueString(String configKey) {
        Config config = CONFIG_BIZ_SERVICE.getConfigByConfigKeyIfExist(configKey);
        String configValue = config.getConfigValue();
        if (StrUtil.isBlank(configValue)) {
            throw new BizRuntimeException(StrUtil.format("获取配置键值为[{}]的字符配置值失败", configKey));
        }
        return configValue;
    }
}
