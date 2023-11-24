package com.youlan.plugin.pay.client.wx;

import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.WxPayParams;

public class WxScanPayClient extends AbstractWxPayClient {

    public WxScanPayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    @Override
    protected PayResponse doPayV2(PayRequest payRequest) {
        return null;
    }

    @Override
    protected PayResponse doPayV3(PayRequest payRequest) {
        return null;
    }

    @Override
    public TradeType tradeType() {
        return TradeType.WX_SCAN;
    }
}
