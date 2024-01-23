package com.youlan.common.core.sensitive.enums;

/**
 * 支持的脱敏类型枚举
 *
 * @author dazer and neusoft and qiaomu
 */
public enum SensitiveType {

    /**
     * 用户id
     */
    USER_ID,
    /**
     * 中文名
     */
    CHINESE_NAME,
    /**
     * 身份证号
     */
    ID_CARD,
    /**
     * 座机号
     */
    FIXED_PHONE,
    /**
     * 手机号
     */
    MOBILE_PHONE,
    /**
     * 地址
     */
    ADDRESS,
    /**
     * 电子邮件
     */
    EMAIL,
    /**
     * 密码
     */
    PASSWORD,
    /**
     * 中国大陆车牌，包含普通车辆、新能源车辆
     */
    CAR_LICENSE,
    /**
     * 银行卡
     */
    BANK_CARD,
    /**
     * IPv4地址
     */
    IPV4,
    /**
     * IPv6地址
     */
    IPV6,
    /**
     * 定义了一个first_mask的规则，只显示第一个字符。
     */
    FIRST_ONLY,
    /**
     * 自定义脱敏类型
     */
    CUSTOM_MASK
}
