package com.youlan.plugin.pay.client.wx;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.youlan.plugin.pay.constant.PayConstant;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.WxPayParams;
import com.youlan.plugin.pay.utils.WxPayUtil;

public class WxJsApiPayClient extends AbstractWxPayClient {

    public WxJsApiPayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    @Override
    public PayResponse doPayV2(PayRequest payRequest) throws WxPayException {
        // 获取openid
        String openId = payRequest.getExtraParamIfExists(PayConstant.PARAM_KEY_OPEN_ID);
        // 初始化V2请求
        WxPayUnifiedOrderRequest unifiedOrderRequest = initOrderRequestV2(payRequest)
                .setOpenid(openId);
        // 创建支付订单
        WxPayMpOrderResult wxPayMpOrderResult = wxPayService.createOrder(unifiedOrderRequest);
        // 返回支付响应
        return WxPayUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), wxPayMpOrderResult);
    }

    @Override
    public PayResponse doPayV3(PayRequest payRequest) throws WxPayException {
        // 获取openid
        String openId = payRequest.getExtraParamIfExists(PayConstant.PARAM_KEY_OPEN_ID);
        WxPayUnifiedOrderV3Request.Payer unifiedOrderPayer = new WxPayUnifiedOrderV3Request.Payer()
                .setOpenid(openId);
        // 初始化V3请求
        WxPayUnifiedOrderV3Request unifiedOrderV3Request = initOrderRequestV3(payRequest)
                .setPayer(unifiedOrderPayer);
        // 创建支付订单
        WxPayUnifiedOrderV3Result.JsapiResult jsapiResult = wxPayService.createOrderV3(TradeTypeEnum.JSAPI, unifiedOrderV3Request);
        // 返回支付响应
        return WxPayUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), jsapiResult);
    }

    @Override
    public TradeType tradeType() {
        return TradeType.WX_JSAPI;
    }
}
