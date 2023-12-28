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
import com.youlan.plugin.pay.enums.PayShowType;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.WxPayParams;
import com.youlan.plugin.pay.utils.PayClientUtil;

public class WxJsApiPayClient extends AbstractWxPayClient {

    public WxJsApiPayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    @Override
    public PayResponse doPayV2(PayRequest payRequest) throws WxPayException {
        // 获取openid
        String openId = payRequest.getExtraParamNotNull(PayConstant.PARAM_KEY_OPEN_ID);
        // 初始化V2请求
        WxPayUnifiedOrderRequest orderRequest = initOrderRequestV2(payRequest)
                .setOpenid(openId);
        // 创建支付订单
        WxPayMpOrderResult wxPayMpOrderResult = wxPayService.createOrder(orderRequest);
        // 返回支付响应
        return PayClientUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), wxPayMpOrderResult, PayShowType.CUSTOM, wxPayMpOrderResult);
    }

    @Override
    public PayResponse doPayV3(PayRequest payRequest) throws WxPayException {
        // 获取openid
        String openId = payRequest.getExtraParamNotNull(PayConstant.PARAM_KEY_OPEN_ID);
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer()
                .setOpenid(openId);
        // 初始化V3请求
        WxPayUnifiedOrderV3Request orderRequest = initOrderRequestV3(payRequest)
                .setPayer(payer);
        // 创建支付订单
        WxPayUnifiedOrderV3Result.JsapiResult orderResult = wxPayService.createOrderV3(TradeTypeEnum.JSAPI, orderRequest);
        // 返回支付响应
        return PayClientUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), orderResult, PayShowType.CUSTOM, orderResult);
    }

    @Override
    public TradeType tradeType() {
        return TradeType.WX_JSAPI;
    }
}
