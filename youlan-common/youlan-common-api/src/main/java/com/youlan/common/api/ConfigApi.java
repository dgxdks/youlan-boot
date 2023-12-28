package com.youlan.common.api;

/**
 * 系统配置接口引用
 */
public interface ConfigApi {
    /**
     * 获取布尔类型系统配置值
     *
     * @param configKey 系统配置键名
     * @return 布尔类型系统配置值
     */
    boolean getConfigValueBoolean(String configKey);

    /**
     * 获取数字类型系统配置值
     *
     * @param configKey 系统配置键名
     * @return 数字类型系统配置值
     */
    Number getConfigValueNumber(String configKey);

    /**
     * 获取字符串类型系统配置值
     *
     * @param configKey 系统配置键名
     * @return 字符串类型系统配置值
     */
    String getConfigValueString(String configKey);
}
