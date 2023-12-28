package com.youlan.plugin.pay.client.wx;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.*;
import com.github.binarywang.wxpay.bean.result.*;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.plugin.pay.client.AbstractPayClient;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.request.PayQueryRequest;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.request.RefundQueryRequest;
import com.youlan.plugin.pay.entity.request.RefundRequest;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.entity.response.RefundResponse;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.enums.RefundStatus;
import com.youlan.plugin.pay.enums.WxApiVersion;
import com.youlan.plugin.pay.params.WxPayParams;
import com.youlan.plugin.pay.utils.PayClientUtil;
import com.youlan.plugin.pay.utils.WxPayUtil;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class AbstractWxPayClient extends AbstractPayClient<WxPayParams> {

    protected WxPayService wxPayService;

    public AbstractWxPayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    protected abstract PayResponse doPayV2(PayRequest payRequest) throws WxPayException;

    protected abstract PayResponse doPayV3(PayRequest payRequest) throws WxPayException;

    protected RefundResponse doRefundV2(RefundRequest refundRequest) throws WxPayException {
        // 构建微信退款请求
        WxPayRefundRequest wxRefundRequest = new WxPayRefundRequest()
                .setOutTradeNo(refundRequest.getOutTradeNo())
                .setOutRefundNo(refundRequest.getOutRefundNo())
                .setRefundFee(WxPayUtil.formatFee(refundRequest.getRefundAmount()))
                .setTotalFee(WxPayUtil.formatFee(refundRequest.getPayAmount()))
                .setRefundDesc(refundRequest.getRefundReason())
                .setNotifyUrl(refundRequest.getNotifyUrl());
        // 发起微信退款请求
        WxPayRefundResult refundResult = wxPayService.refundV2(wxRefundRequest);
        String resultCode = refundResult.getResultCode();
        // V2版本退款是异步回调通知，如果退款成功则返回等待退款状态
        if (StrUtil.equals("SUCCESS", resultCode)) {
            return PayClientUtil.createRefundWaitingResponse(refundRequest.getOutRefundNo(), refundResult.getRefundId(),
                    refundResult);
        }
        return PayClientUtil.createRefundFailedResponse(refundRequest.getOutRefundNo(), null, null, refundResult);
    }

    protected RefundResponse doRefundV3(RefundRequest refundRequest) throws WxPayException {
        // 构建微信退款请求
        WxPayRefundV3Request wxRefundRequest = new WxPayRefundV3Request()
                .setOutTradeNo(refundRequest.getOutTradeNo())
                .setOutRefundNo(refundRequest.getOutRefundNo())
                .setAmount(WxPayUtil.formatRefundAmount(refundRequest.getPayAmount()))
                .setReason(refundRequest.getRefundReason())
                .setNotifyUrl(refundRequest.getNotifyUrl());
        // 发起微信退款请求
        WxPayRefundV3Result refundResult = wxPayService.refundV3(wxRefundRequest);
        String status = refundResult.getStatus();
        switch (status) {
            case "SUCCESS":
                // V3版本退款为同步请求，如果退款成功则返回退款成功状态
                return PayClientUtil.createRefundSuccessResponse(refundRequest.getOutRefundNo(), refundResult.getRefundId(),
                        refundResult, WxPayUtil.parseTimeV3(refundResult.getSuccessTime()));
            case "PROCESSING":
                return PayClientUtil.createRefundWaitingResponse(refundRequest.getOutRefundNo(), refundResult.getRefundId(), refundResult);
            default:
                return PayClientUtil.createRefundFailedResponse(refundRequest.getOutRefundNo(), null, null, refundResult);
        }
    }

    protected PayResponse doPayQueryV2(PayQueryRequest payQueryRequest) throws WxPayException {
        // 发起微信支付查询请求
        WxPayOrderQueryRequest queryRequest = WxPayOrderQueryRequest.newBuilder()
                .outTradeNo(payQueryRequest.getOutTradeNo())
                .build();
        WxPayOrderQueryResult queryResult = wxPayService.queryOrder(queryRequest);
        String tradeState = queryResult.getTradeState();
        // 转换支付状态
        PayStatus payStatus = WxPayUtil.tradeStateToPayStatus(tradeState);
        // 返回支付查询响应
        return PayClientUtil.createPayResponse(payStatus, queryResult.getOutTradeNo(), queryRequest.getTransactionId(), queryResult.getOpenid(),
                queryResult, WxPayUtil.parseTimeV2(queryResult.getTimeEnd()));
    }

    protected PayResponse doPayQueryV3(PayQueryRequest payQueryRequest) throws WxPayException {
        WxPayOrderQueryV3Request queryRequest = new WxPayOrderQueryV3Request()
                .setOutTradeNo(payQueryRequest.getOutTradeNo());
        // 发起微信支付查询请求
        WxPayOrderQueryV3Result queryResult = wxPayService.queryOrderV3(queryRequest);
        String tradeState = queryResult.getTradeState();
        // 转换支付状态
        PayStatus payStatus = WxPayUtil.tradeStateToPayStatus(tradeState);
        // 返回支付查询响应
        String openId = ObjectUtil.isNotNull(queryResult.getPayer()) ? queryResult.getPayer().getOpenid() : null;
        return PayClientUtil.createPayResponse(payStatus, queryResult.getOutTradeNo(), queryResult.getTransactionId(), openId,
                queryResult, WxPayUtil.parseTimeV3(queryResult.getSuccessTime()));
    }

    protected PayResponse doPayNotifyParseV2(Map<String, String> params, String body) throws Exception {
        // 发起支付回调解析
        WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(body);
        String resultCode = notifyResult.getResultCode();
        // 转换支付状态
        PayStatus payStatus = StrUtil.equals("SUCCESS", resultCode) ? PayStatus.SUCCESS : PayStatus.CLOSED;
        // 返回支付回调
        return PayClientUtil.createPayResponse(payStatus, notifyResult.getOutTradeNo(), notifyResult.getTransactionId(), notifyResult.getOpenid(),
                notifyResult, WxPayUtil.parseTimeV2(notifyResult.getTimeEnd()));
    }

    protected PayResponse doPayNotifyParseV3(Map<String, String> params, String body) throws Exception {
        // 发起支付回调解析
        WxPayOrderNotifyV3Result wxNotifyResult = wxPayService.parseOrderNotifyV3Result(body, null);
        WxPayOrderNotifyV3Result.DecryptNotifyResult notifyResult = wxNotifyResult.getResult();
        // 转换支付状态
        String tradeState = notifyResult.getTradeState();
        PayStatus payStatus = WxPayUtil.tradeStateToPayStatus(tradeState);
        // 返回支付回调
        String openId = ObjectUtil.isNotNull(notifyResult.getPayer()) ? notifyResult.getPayer().getOpenid() : null;
        return PayClientUtil.createPayResponse(payStatus, notifyResult.getOutTradeNo(), notifyResult.getTransactionId(),
                openId, notifyResult, WxPayUtil.parseTimeV3(notifyResult.getSuccessTime()));
    }

    protected RefundResponse doRefundQueryV2(RefundQueryRequest refundQueryRequest) throws WxPayException {
        // 发起微信退款查询请求
        WxPayRefundQueryRequest wxQueryRequest = WxPayRefundQueryRequest.newBuilder()
                .outTradeNo(refundQueryRequest.getOutTradeNo())
                .outRefundNo(refundQueryRequest.getOutRefundNo())
                .build();
        WxPayRefundQueryResult refundQueryResult = wxPayService.refundQuery(wxQueryRequest);
        // 先判断响应是否成功
        String resultCode = refundQueryResult.getResultCode();
        if (!StrUtil.equals("SUCCESS", resultCode)) {
            return PayClientUtil.createRefundResponse(RefundStatus.WAITING, refundQueryRequest.getOutRefundNo(),
                    null, refundQueryResult, null);
        }
        // 判断退款是否成功
        WxPayRefundQueryResult.RefundRecord refundRecord = CollUtil.findOne(refundQueryResult.getRefundRecords(), record -> StrUtil.equals(refundQueryRequest.getOutRefundNo(), record.getOutRefundNo()));
        // 未找到退款记录则返回退款失败
        if (ObjectUtil.isNull(refundRecord)) {
            return PayClientUtil.createRefundResponse(RefundStatus.FAILED, refundQueryRequest.getOutRefundNo(),
                    null, refundQueryResult, null);
        }
        // 返回退款查询相应
        String refundStatus = refundRecord.getRefundStatus();
        switch (refundStatus) {
            // 退款成功
            case "SUCCESS":
                return PayClientUtil.createRefundResponse(RefundStatus.SUCCESS, refundRecord.getOutRefundNo(),
                        refundRecord.getRefundId(), refundQueryResult, WxPayUtil.parseRefundSuccessTime(refundRecord.getRefundSuccessTime()));
            // 退款处理中
            case "PROCESSING":
                return PayClientUtil.createRefundResponse(RefundStatus.WAITING, refundRecord.getOutRefundNo(),
                        refundRecord.getRefundId(), refundQueryResult, null);
            // 退款失败
            case "FAIL":
                // 转入代发
            case "CHANGE":
                return PayClientUtil.createRefundResponse(RefundStatus.FAILED, refundRecord.getOutRefundNo(),
                        null, refundQueryResult, null);
            default:
                throw new BizRuntimeException(StrUtil.format("未知的退款状态：{}", refundStatus));
        }
    }

    protected RefundResponse doRefundQueryV3(RefundQueryRequest refundQueryRequest) throws WxPayException {
        // 发起微信退款查询请求
        WxPayRefundQueryV3Request wxRefundQueryRequest = new WxPayRefundQueryV3Request();
        wxRefundQueryRequest.setOutRefundNo(refundQueryRequest.getOutRefundNo());
        WxPayRefundQueryV3Result refundedQueryResult = wxPayService.refundQueryV3(wxRefundQueryRequest);
        String status = refundedQueryResult.getStatus();
        switch (status) {
            case "SUCCESS":
                return PayClientUtil.createRefundResponse(RefundStatus.SUCCESS, refundQueryRequest.getOutRefundNo(),
                        refundedQueryResult.getRefundId(), refundedQueryResult, WxPayUtil.parseTimeV3(refundedQueryResult.getSuccessTime()));
            case "PROCESSING":
                return PayClientUtil.createRefundResponse(RefundStatus.WAITING, refundQueryRequest.getOutRefundNo(),
                        refundedQueryResult.getRefundId(), refundedQueryResult, null);
            case "ABNORMAL":
            case "CLOSED":
                return PayClientUtil.createRefundResponse(RefundStatus.FAILED, refundQueryRequest.getOutRefundNo(),
                        null, refundedQueryResult, null);
            default:
                throw new BizRuntimeException(StrUtil.format("未知的退款状态：{}", status));
        }
    }

    protected RefundResponse doRefundNotifyParseV2(Map<String, String> params, String body) throws Exception {
        // 发起退款回调解析
        WxPayRefundNotifyResult refundNotifyResult = wxPayService.parseRefundNotifyResult(body);
        WxPayRefundNotifyResult.ReqInfo reqInfo = refundNotifyResult.getReqInfo();
        // 返回退款回调响应
        if (StrUtil.equals("SUCCESS", reqInfo.getRefundStatus())) {
            return PayClientUtil.createRefundResponse(RefundStatus.SUCCESS, reqInfo.getOutRefundNo(),
                    reqInfo.getRefundId(), refundNotifyResult, WxPayUtil.parseTimeV2(reqInfo.getSuccessTime()));
        }
        return PayClientUtil.createRefundResponse(RefundStatus.FAILED, reqInfo.getOutRefundNo(),
                null, refundNotifyResult, null);
    }

    protected RefundResponse doRefundNotifyParseV3(Map<String, String> params, String body) throws Exception {
        WxPayRefundNotifyV3Result refundNotifyResult = wxPayService.parseRefundNotifyV3Result(body, null);
        WxPayRefundNotifyV3Result.DecryptNotifyResult notifyResult = refundNotifyResult.getResult();
        if (StrUtil.equals("SUCCESS", notifyResult.getRefundStatus())) {
            return PayClientUtil.createRefundResponse(RefundStatus.SUCCESS, notifyResult.getOutRefundNo(),
                    notifyResult.getRefundId(), refundNotifyResult, WxPayUtil.parseTimeV3(notifyResult.getSuccessTime()));
        }
        return PayClientUtil.createRefundResponse(RefundStatus.FAILED, notifyResult.getOutRefundNo(),
                null, refundNotifyResult, null);
    }

    protected WxPayUnifiedOrderRequest initOrderRequestV2(PayRequest payRequest) {
        return WxPayUnifiedOrderRequest.newBuilder()
                .outTradeNo(payRequest.getOutTradeNo())
                .body(payRequest.getSubject())
                .detail(payRequest.getDetail())
                .totalFee(WxPayUtil.formatFee(payRequest.getAmount()))
                .timeExpire(WxPayUtil.formatTimeV2(payRequest.getExpireTime()))
                .spbillCreateIp(payRequest.getClientIp())
                .notifyUrl(payRequest.getNotifyUrl())
                .build();
    }

    protected WxPayUnifiedOrderV3Request initOrderRequestV3(PayRequest payRequest) {
        WxPayUnifiedOrderV3Request unifiedOrderV3Request = new WxPayUnifiedOrderV3Request()
                .setOutTradeNo(payRequest.getOutTradeNo())
                .setDescription(payRequest.getSubject())
                .setTimeExpire(WxPayUtil.formatTimeV3(payRequest.getExpireTime()))
                .setNotifyUrl(payRequest.getNotifyUrl())
                .setAmount(WxPayUtil.formatPayAmount(payRequest.getAmount()));
        // 设置用户IP
        WxPayUnifiedOrderV3Request.SceneInfo unifiedOrderV3RequestSceneInfo = new WxPayUnifiedOrderV3Request.SceneInfo();
        unifiedOrderV3RequestSceneInfo.setPayerClientIp(payRequest.getClientIp());
        return unifiedOrderV3Request;
    }


    @Override
    public final RefundResponse doRefund(RefundRequest refundRequest) {
        try {
            switch (this.payParams.getApiVersion()) {
                case V2:
                    return this.doRefundV2(refundRequest);
                case V3:
                    return this.doRefundV3(refundRequest);
                default:
                    throw new BizRuntimeException(ApiResultCode.E0010);
            }
        } catch (WxPayException e) {
            return PayClientUtil.createRefundFailedResponse(refundRequest.getOutRefundNo(), WxPayUtil.getErrorCode(e),
                    WxPayUtil.getErrorMsg(e), e.getXmlString());
        }
    }

    @Override
    public final PayResponse doPay(PayRequest payRequest) {
        WxApiVersion apiVersion = this.payParams.getApiVersion();
        try {
            switch (apiVersion) {
                case V2:
                    return this.doPayV2(payRequest);
                case V3:
                    return this.doPayV3(payRequest);
                default:
                    throw new BizRuntimeException(ApiResultCode.E0010);
            }
        } catch (WxPayException e) {
            return PayClientUtil.createPayClosedResponse(payRequest.getOutTradeNo(), WxPayUtil.getErrorCode(e),
                    WxPayUtil.getErrorMsg(e), e.getXmlString());
        }
    }

    @Override
    protected final PayResponse doPayQuery(PayQueryRequest payQueryRequest) throws Exception {
        WxApiVersion apiVersion = this.payParams.getApiVersion();
        try {
            switch (apiVersion) {
                case V2:
                    return this.doPayQueryV2(payQueryRequest);
                case V3:
                    return this.doPayQueryV3(payQueryRequest);
                default:
                    throw new BizRuntimeException(ApiResultCode.E0010);
            }
        } catch (WxPayException e) {
            // 需要拦截订单不存在的异常
            if (StrUtil.equalsAny(e.getErrCode(), "ORDERNOTEXIST", "ORDER_NOT_EXIST")) {
                return PayClientUtil.createPayClosedResponse(payQueryRequest.getOutTradeNo(), WxPayUtil.getErrorCode(e), WxPayUtil.getErrorMsg(e), e.getXmlString());
            }
            throw e;
        }
    }

    @Override
    protected final PayResponse doPayNotifyParse(Map<String, String> params, String body) throws Exception {
        WxApiVersion apiVersion = this.payParams.getApiVersion();
        switch (apiVersion) {
            case V2:
                return this.doPayNotifyParseV2(params, body);
            case V3:
                return this.doPayNotifyParseV3(params, body);
            default:
                throw new BizRuntimeException(ApiResultCode.E0010);
        }
    }

    @Override
    protected final RefundResponse doRefundQuery(RefundQueryRequest refundQueryRequest) throws Exception {
        WxApiVersion apiVersion = this.payParams.getApiVersion();
        try {
            switch (apiVersion) {
                case V2:
                    return this.doRefundQueryV2(refundQueryRequest);
                case V3:
                    return this.doRefundQueryV3(refundQueryRequest);
                default:
                    throw new BizRuntimeException(ApiResultCode.E0010);
            }
        } catch (WxPayException e) {
            // 需要拦截订单不存在的异常
            if (StrUtil.equalsAny(e.getErrCode(), "REFUNDNOTEXIST", "RESOURCE_NOT_EXISTS")) {
                return PayClientUtil.createRefundFailedResponse(refundQueryRequest.getOutRefundNo(), WxPayUtil.getErrorCode(e),
                        WxPayUtil.getErrorMsg(e), e.getXmlString());
            }
            throw e;
        }
    }

    @Override
    protected final RefundResponse doRefundNotifyParse(Map<String, String> params, String body) throws Exception {
        WxApiVersion apiVersion = this.payParams.getApiVersion();
        switch (apiVersion) {
            case V2:
                return this.doRefundNotifyParseV2(params, body);
            case V3:
                return this.doRefundNotifyParseV3(params, body);
            default:
                throw new BizRuntimeException(ApiResultCode.E0010);
        }
    }

    @Override
    protected void doStartClient() {
        WxPayConfig wxPayconfig = WxPayUtil.createWxPayconfig(payParams, tradeType());
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayconfig);
        this.wxPayService = wxPayService;
    }

    @Override
    protected void doStopClient() {
    }
}
