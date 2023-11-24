package com.youlan.plugin.pay.factory;

import cn.hutool.core.util.ReflectUtil;
import com.youlan.plugin.pay.client.AbstractPayClient;
import com.youlan.plugin.pay.client.PayClient;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.PayParams;
import com.youlan.plugin.pay.utils.PayConfigUtil;

public class PayClientFactory {

    /**
     * 创建支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    public static PayClient createPayClient(PayConfig payConfig, TradeType tradeType) {
        Class<? extends AbstractPayClient<? extends PayParams>> clientClass = tradeType.getClientClass();
        PayParams payParams = PayConfigUtil.getPayParams(payConfig.getParams(), payConfig.getType());
        return ReflectUtil.newInstance(clientClass, payConfig, payParams);
    }

    /**
     * 创建支付客户端
     *
     * @param payConfig 支付配置
     * @param tradeType 交易类型
     * @return 支付客户端
     */
    public static PayClient createPayClient(PayConfig payConfig, String tradeType) {
        return createPayClient(payConfig, TradeType.getTradeType(tradeType));
    }
}
