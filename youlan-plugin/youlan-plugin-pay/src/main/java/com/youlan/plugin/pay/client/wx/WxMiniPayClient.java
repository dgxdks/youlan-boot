package com.youlan.plugin.pay.client.wx;

import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.WxPayParams;

public class WxMiniPayClient extends WxJsApiPayClient {

    public WxMiniPayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    @Override
    public TradeType tradeType() {
        return TradeType.WX_MINI;
    }
}
