package com.youlan.plugin.pay.constant;

public class PayConstant {

    // ******************** 参数键名 ********************
    public static final String PARAM_KEY_OPEN_ID = "openid";

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
     * 已失败
     */
    public static final String PAY_STATUS_FAILED = "3";
    /**
     * 已关闭
     */
    public static final String PAY_STATUS_CLOSED = "4";
    /**
     * 已退款
     */
    public static final String PAY_STATUS_REFUND = "5";

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

    // ******************** 交易类型 ********************
    public static final String TRADE_TYPE_WX_JSAPI = "WX_JSAPI";
    public static final String TRADE_TYPE_WX_APP = "WX_APP";
    public static final String TRADE_TYPE_WX_H5 = "WX_H5";
    public static final String TRADE_TYPE_WX_NATIVE = "WX_NATIVE";
    public static final String TRADE_TYPE_WX_MINI = "WX_MINI";
    public static final String TRADE_TYPE_WX_SCAN = "WX_SCAN";

    // ******************** 币类型 ********************
    public static final String CURRENCY_CNY = "CNY";
}
