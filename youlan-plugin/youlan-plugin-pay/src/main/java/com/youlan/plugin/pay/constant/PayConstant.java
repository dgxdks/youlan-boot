package com.youlan.plugin.pay.constant;

public class PayConstant {

    // ******************** 缓存前缀 ********************
    /**
     * 订单编号缓存前缀
     */
    public static final String REDIS_PREFIX_ORDER_NO = "pay:order_no:";
    /**
     * 支付回调缓存前缀
     */
    public static final String REDIS_PREFIX_PAY_NOTIFY = "pay:notify";
    /**
     * 退款订单同步缓存前缀
     */
    public static final String REDIS_PREFIX_REFUND_ORDER_SYNC = "pay:refund_order_sync";
    // ******************** 参数键名 ********************
    public static final String PARAM_KEY_OPEN_ID = "openid";
    public static final String PARAM_KEY_AUTH_CODE = "auth_code";

    // ******************** 支付状态 ********************
    /**
     * 待支付
     */
    public static final String PAY_STATUS_WAITING = "1";
    /**
     * 已支付
     */
    public static final String PAY_STATUS_SUCCESS = "2";
    /**
     * 已关闭
     */
    public static final String PAY_STATUS_CLOSED = "3";
    /**
     * 已退款
     */
    public static final String PAY_STATUS_REFUND = "4";

    // ******************** 退款状态 ********************
    /**
     * 待退款
     */
    public static final String REFUND_STATUS_WAITING = "1";
    /**
     * 已退款
     */
    public static final String REFUND_STATUS_SUCCESS = "2";
    /**
     * 已失败
     */
    public static final String REFUND_STATUS_FAILED = "3";

    // ******************** 回调类型 ********************
    /**
     * 支付回调
     */
    public static final String NOTIFY_TYPE_PAY = "1";
    /**
     * 退款回调
     */
    public static final String NOTIFY_TYPE_REFUND = "2";

    // ******************** 回调状态 ********************
    /**
     * 等待回调
     */
    public static final String NOTIFY_STATUS_WAITING = "1";
    /**
     * 回调成功(请求成功且对方返回成功)
     */
    public static final String NOTIFY_STATUS_SUCCESS = "2";
    /**
     * 回调失败(超过重试次数后最终失败)
     */
    public static final String NOTIFY_STATUS_FAILED = "3";
    /**
     * 请求成功(请求成功但对方返回失败)
     */
    public static final String NOTIFY_STATUS_REQUEST_SUCCESS = "4";
    /**
     * 请求失败
     */
    public static final String NOTIFY_STATUS_REQUEST_FAILED = "5";

    // ******************** 回调频率 ********************
    public static final int[] NOTIFY_FREQUENCY = {15, 15, 30, 180, 1800, 1800, 1800, 3600};

    // ******************** 交易类型 ********************
    /**
     * 微信JSAPI支付
     */
    public static final String TRADE_TYPE_WX_JSAPI = "WX_JSAPI";
    /**
     * 微信APP支付
     */
    public static final String TRADE_TYPE_WX_APP = "WX_APP";
    /**
     * 微信H5支付
     */
    public static final String TRADE_TYPE_WX_H5 = "WX_H5";
    /**
     * 微信Native支付
     */
    public static final String TRADE_TYPE_WX_NATIVE = "WX_NATIVE";
    /**
     * 微信小程序支付
     */
    public static final String TRADE_TYPE_WX_MINI = "WX_MINI";
    /**
     * 微信扫码支付
     */
    public static final String TRADE_TYPE_WX_SCAN = "WX_SCAN";

    // ******************** 线程池 ********************
    public static final String PYA_NOTIFY_THREAD_POOL_NAME = "payNotifyThreadPool";
    public static final String PAY_NOTIFY_THREAD_NAME_PREFIX = "pay-notify-";

    // ******************** 币类型 ********************
    public static final String CURRENCY_CNY = "CNY";
}
