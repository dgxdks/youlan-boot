package com.youlan.plugin.pay.client.wx;

import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.enums.PayShowType;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.WxPayParams;
import com.youlan.plugin.pay.utils.PayClientUtil;

public class WxAppPayClient extends AbstractWxPayClient {

    public WxAppPayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    @Override
    protected PayResponse doPayV2(PayRequest payRequest) throws WxPayException {
        // 创建V2请求
        WxPayUnifiedOrderRequest orderRequest = initOrderRequestV2(payRequest);
        // 创建支付订单
        WxPayAppOrderResult orderResult = wxPayService.createOrder(orderRequest);
        // 返回支付响应
        return PayClientUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), orderResult, PayShowType.CUSTOM, orderResult);
    }

    @Override
    protected PayResponse doPayV3(PayRequest payRequest) throws WxPayException {
        // 创建V3请求
        WxPayUnifiedOrderV3Request orderRequest = initOrderRequestV3(payRequest);
        // 创建支付订单
        WxPayUnifiedOrderV3Result.AppResult orderResult = wxPayService.createOrderV3(TradeTypeEnum.APP, orderRequest);
        // 返回支付响应
        return PayClientUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), orderResult, PayShowType.CUSTOM, orderResult);
    }

    @Override
    public TradeType tradeType() {
        return TradeType.WX_APP;
    }
}
