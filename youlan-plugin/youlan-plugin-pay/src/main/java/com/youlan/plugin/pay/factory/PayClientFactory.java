package com.youlan.plugin.pay.factory;

import com.youlan.plugin.pay.client.PayClient;
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

}
