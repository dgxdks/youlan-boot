package com.youlan.plugin.pay.factory;

import com.youlan.plugin.pay.client.PayClient;
import com.youlan.plugin.pay.client.wx.*;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.TradeType;

public interface PayClientFactory {

    /**
     * 创建支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    PayClient createPayClient(PayConfig payConfig, TradeType tradeType);

    /**
     * 创建微信APP支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    default WxAppPayClient createWxAppPayClient(PayConfig payConfig, TradeType tradeType) {
        return (WxAppPayClient) createPayClient(payConfig, tradeType);
    }

    /**
     * 创建微信H5支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    default WxH5PayClient createWxH5PayClient(PayConfig payConfig, TradeType tradeType) {
        return (WxH5PayClient) createPayClient(payConfig, tradeType);
    }

    /**
     * 创建微信JSAPI支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    default WxJsApiPayClient createWxJsApiPayClient(PayConfig payConfig, TradeType tradeType) {
        return (WxJsApiPayClient) createPayClient(payConfig, tradeType);
    }

    /**
     * 创建微信小程序支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    default WxMiniPayClient createWxMiniPayClient(PayConfig payConfig, TradeType tradeType) {
        return (WxMiniPayClient) createPayClient(payConfig, tradeType);
    }

    /**
     * 创建微信Native支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    default WxNativePayClient createWxNativePayClient(PayConfig payConfig, TradeType tradeType) {
        return (WxNativePayClient) createPayClient(payConfig, tradeType);
    }

    /**
     * 创建微信扫码支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    default WxScanPayClient createWxScanPayClient(PayConfig payConfig, TradeType tradeType) {
        return (WxScanPayClient) createPayClient(payConfig, tradeType);
    }
}
