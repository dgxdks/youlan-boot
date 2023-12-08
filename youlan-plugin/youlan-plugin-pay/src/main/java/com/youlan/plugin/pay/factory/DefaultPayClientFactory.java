package com.youlan.plugin.pay.factory;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.youlan.plugin.pay.client.AbstractPayClient;
import com.youlan.plugin.pay.client.PayClient;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.PayParams;
import com.youlan.plugin.pay.utils.PayUtil;

public class DefaultPayClientFactory implements PayClientFactory {

    @Override
    public PayClient createPayClient(PayConfig payConfig, TradeType tradeType) {
        Assert.notNull(tradeType, "交易类型不能为空");
        Class<? extends AbstractPayClient<? extends PayParams>> clientClass = tradeType.getClientClass();
        PayParams payParams = PayUtil.parsePayParams(payConfig);
        AbstractPayClient<? extends PayParams> payClient = ReflectUtil.newInstance(clientClass, payConfig, payParams);
        payClient.startClient();
        return payClient;
    }

}
