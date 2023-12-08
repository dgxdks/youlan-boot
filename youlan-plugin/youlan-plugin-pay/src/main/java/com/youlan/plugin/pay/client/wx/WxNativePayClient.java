package com.youlan.plugin.pay.client.wx;

import com.github.binarywang.wxpay.bean.order.WxPayNativeOrderResult;
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
import com.youlan.plugin.pay.utils.WxPayUtil;

public class WxNativePayClient extends AbstractWxPayClient {

    public WxNativePayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    @Override
    protected PayResponse doPayV2(PayRequest payRequest) throws WxPayException {
        // 初始化V2请求
        WxPayUnifiedOrderRequest orderRequest = initOrderRequestV2(payRequest);
        // 创建支付订单
        WxPayNativeOrderResult orderResult = wxPayService.createOrder(orderRequest);
        return WxPayUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), orderResult, PayShowType.QR_CODE, orderResult);
    }

    @Override
    protected PayResponse doPayV3(PayRequest payRequest) throws WxPayException {
        // 初始化V3请求
        WxPayUnifiedOrderV3Request orderRequest = initOrderRequestV3(payRequest);
        // 创建支付订单
        WxPayUnifiedOrderV3Result orderResult = wxPayService.createOrderV3(TradeTypeEnum.NATIVE, orderRequest);
        return WxPayUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), orderResult, PayShowType.QR_CODE, orderResult);
    }

    @Override
    public TradeType tradeType() {
        return TradeType.WX_NATIVE;
    }
}
