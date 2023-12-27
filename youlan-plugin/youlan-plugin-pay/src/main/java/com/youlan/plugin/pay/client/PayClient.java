package com.youlan.plugin.pay.client;

import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.request.PayQueryRequest;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.request.RefundQueryRequest;
import com.youlan.plugin.pay.entity.request.RefundRequest;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.entity.response.RefundResponse;
import com.youlan.plugin.pay.enums.TradeType;

import java.util.Map;

public interface PayClient {

    /**
     * 支付
     *
     * @param payRequest 支付请求
     * @return 支付响应
     */
    PayResponse pay(PayRequest payRequest);

    /**
     * 支付查询
     *
     * @param payQueryRequest 支付查询请求
     * @return 支付响应
     */
    PayResponse payQuery(PayQueryRequest payQueryRequest);


    /**
     * 支付回调解析
     *
     * @param params 路径参数
     * @param body   请求体
     * @return 支付响应
     */
    PayResponse payNotifyParse(Map<String, String> params, String body);

    /**
     * 退款
     *
     * @param refundRequest 退款请求
     * @return 退款请求
     */
    RefundResponse refund(RefundRequest refundRequest);

    /**
     * 退款查询
     *
     * @param refundQueryRequest 退款查询请求
     * @return 退款响应
     */
    RefundResponse refundQuery(RefundQueryRequest refundQueryRequest);

    /**
     * 退款回调解析
     *
     * @param params 路径参数
     * @param body   请求体
     * @return 退款响应
     */
    RefundResponse refundNotifyParse(Map<String, String> params, String body);

    /**
     * 启动客户端
     */
    void startClient();

    /**
     * 停止客户端
     */
    void stopClient();

    /**
     * 交易类型
     *
     * @return 交易类型
     */
    TradeType tradeType();

    /**
     * 支付配置
     *
     * @return 支付配置
     */
    PayConfig payConfig();
}
