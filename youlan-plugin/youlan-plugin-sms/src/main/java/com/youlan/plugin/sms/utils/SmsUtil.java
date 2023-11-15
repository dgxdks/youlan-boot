package com.youlan.plugin.sms.utils;

import static com.youlan.plugin.sms.constant.SmsConstant.REDIS_PREFIX_SMS_DAO;
import static com.youlan.plugin.sms.constant.SmsConstant.REDIS_PREFIX_SMS_SUPPLIER;

public class SmsUtil {
    /**
     * 获取短信厂商 redis key
     */
    public static String getSmsSupplierRedisKey(String configId) {
        return REDIS_PREFIX_SMS_SUPPLIER + configId;
    }

    /**
     * /
     * 获取短信厂商 redis key
     */
    public static String getSmsDaoRedisKey(String key) {
        return REDIS_PREFIX_SMS_DAO + key;
    }
}
