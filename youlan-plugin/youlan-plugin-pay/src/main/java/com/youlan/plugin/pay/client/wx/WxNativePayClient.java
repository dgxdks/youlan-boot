package com.youlan.plugin.pay.client.wx;

import com.github.binarywang.wxpay.bean.order.WxPayNativeOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.enums.PayShowType;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.WxPayParams;
import com.youlan.plugin.pay.utils.PayClientUtil;

public class WxNativePayClient extends AbstractWxPayClient {

    public WxNativePayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    @Override
    protected PayResponse doPayV2(PayRequest payRequest) throws WxPayException {
        // 初始化V2请求
        WxPayUnifiedOrderRequest orderRequest = initOrderRequestV2(payRequest);
        // 设置productId(此处暂时使用外部交易订单号，实际项目需要根据业务需求修改)
        orderRequest.setProductId(payRequest.getOutTradeNo());
        // 创建支付订单
        WxPayNativeOrderResult orderResult = wxPayService.createOrder(orderRequest);
        // 支付信息强制只返回支付链接
        return PayClientUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), orderResult, PayShowType.QR_CODE, orderResult.getCodeUrl());
    }

    @Override
    protected PayResponse doPayV3(PayRequest payRequest) throws WxPayException {
        // 初始化V3请求
        WxPayUnifiedOrderV3Request orderRequest = initOrderRequestV3(payRequest);
        // 创建支付订单(会直接返回支付链接)
        String orderResult = wxPayService.createOrderV3(TradeTypeEnum.NATIVE, orderRequest);
        return PayClientUtil.createPayWaitingResponse(payRequest.getOutTradeNo(), orderResult, PayShowType.QR_CODE, orderResult);
    }

    @Override
    public TradeType tradeType() {
        return TradeType.WX_NATIVE;
    }
}
