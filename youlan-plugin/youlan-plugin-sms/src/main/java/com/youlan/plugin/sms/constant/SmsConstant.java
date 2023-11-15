package com.youlan.plugin.sms.constant;

public class SmsConstant {

    // ******************** 缓存前缀 ********************
    /**
     * 短信厂商缓存前缀
     */
    public static final String REDIS_PREFIX_SMS_SUPPLIER = "sms_supplier:";

    /**
     * 短信持久化存储缓存前缀
     */
    public static final String REDIS_PREFIX_SMS_DAO = "sms_dao:";


    // ******************** 短信类型 ********************

    /**
     * 标准短信
     */
    public static final String SMS_TYPE_NORMAL = "1";
    /**
     * 异步短信
     */
    public static final String SMS_TYPE_ASYNC = "2";

    /**
     * 延迟短信
     */
    public static final String SMS_TYPE_DELAYED = "3";

    // ******************** 发送类型 ********************
    /**
     * 单个发送
     */
    public static final String SEND_TYPE_SINGLE = "1";
    /**
     * 批量发送
     */
    public static final String SEND_TYPE_BATCH = "2";

    // ******************** 发送状态 ********************
    /**
     * 成功
     */
    public static final String SEND_STATUS_SUCCESS = "1";
    /**
     * 失败
     */
    public static final String SEND_STATUS_FAILED = "2";

    /**
     * 是否成功转发送状态
     *
     * @param success 是否成功
     * @return 发送状态
     */
    public static String booleanToSendStatus(boolean success) {
        return success ? SmsConstant.SEND_STATUS_SUCCESS : SmsConstant.SEND_STATUS_FAILED;
    }
}
