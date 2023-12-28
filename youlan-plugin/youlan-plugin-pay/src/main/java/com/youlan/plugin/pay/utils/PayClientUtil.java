package com.youlan.plugin.pay.utils;

import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.entity.response.RefundResponse;
import com.youlan.plugin.pay.enums.PayShowType;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.enums.RefundStatus;

import java.util.Date;

public class PayClientUtil {

    /**
     * 创建退款响应
     *
     * @param refundStatus 退款状态
     * @param outRefundNo  外部退款订单号
     * @param refundNo     退款订单号
     * @param rawData      原始数据
     * @param successTime  成功时间
     * @return 退款响应
     */
    public static RefundResponse createRefundResponse(RefundStatus refundStatus, String outRefundNo, String refundNo, Object rawData, Date successTime) {
        return (RefundResponse) new RefundResponse()
                .setRefundStatus(refundStatus)
                .setOutRefundNo(outRefundNo)
                .setRefundNo(refundNo)
                .setRawData(rawData)
                .setSuccessTime(successTime);
    }

    /**
     * 创建支付响应
     *
     * @param payStatus   支付状态
     * @param outTradeNo  外部交易订单号
     * @param tradeNo     交易订单号
     * @param clientId    客户端ID
     * @param rawData     原始数据
     * @param successTime 成功时间
     * @return 支付响应
     */
    public static PayResponse createPayResponse(PayStatus payStatus, String outTradeNo, String tradeNo, String clientId, Object rawData, Date successTime) {
        return (PayResponse) new PayResponse()
                .setPayStatus(payStatus)
                .setOutTradeNo(outTradeNo)
                .setTradeNo(tradeNo)
                .setClientId(clientId)
                .setRawData(rawData)
                .setSuccessTime(successTime);
    }

    /**
     * 创建待支付响应
     *
     * @param outTradeNo  外部交易订单号
     * @param rawData     原始数据
     * @param payShowType 支付展示类型
     * @param payInfo     支付信息
     * @return 待支付响应
     */
    public static PayResponse createPayWaitingResponse(String outTradeNo, Object rawData, PayShowType payShowType, Object payInfo) {
        return (PayResponse) new PayResponse()
                .setPayShowType(payShowType)
                .setPayInfo(payInfo)
                .setPayStatus(PayStatus.WAITING)
                .setOutTradeNo(outTradeNo)
                .setRawData(rawData);
    }

    /**
     * 创建支付关闭响应
     *
     * @param outTradeNo 外部订单号
     * @param errorCode  错误码
     * @param errorMsg   错误信息
     * @param rawData    原始数据
     * @return 失败响应
     */
    public static PayResponse createPayClosedResponse(String outTradeNo, String errorCode, String errorMsg, Object rawData) {
        return (PayResponse) new PayResponse()
                .setPayStatus(PayStatus.CLOSED)
                .setOutTradeNo(outTradeNo)
                .setErrorCode(errorCode)
                .setErrorMsg(errorMsg)
                .setRawData(rawData);
    }

    /**
     * 创建支付成功响应
     *
     * @param outTradeNo  外部交易订单号
     * @param tradeNo     交易订单号
     * @param clientId    客户端ID
     * @param rawData     原始数据
     * @param successTime 成功时间
     * @return 支付成功响应
     */
    public static PayResponse createPaySuccessResponse(String outTradeNo, String tradeNo, String clientId, Object rawData, PayShowType payShowType, Date successTime) {
        return (PayResponse) new PayResponse()
                .setPayShowType(payShowType)
                .setPayStatus(PayStatus.SUCCESS)
                .setOutTradeNo(outTradeNo)
                .setTradeNo(tradeNo)
                .setClientId(clientId)
                .setRawData(rawData)
                .setSuccessTime(successTime);
    }

    /**
     * 创建退款失败响应
     *
     * @param outRefundNo 外部退款号
     * @param errorCode   错误码
     * @param errorMsg    错误信息
     * @param rawData     原始数据
     * @return 失败响应
     */
    public static RefundResponse createRefundFailedResponse(String outRefundNo, String errorCode, String errorMsg, Object rawData) {
        return (RefundResponse) new RefundResponse()
                .setRefundStatus(RefundStatus.FAILED)
                .setOutRefundNo(outRefundNo)
                .setErrorCode(errorCode)
                .setErrorMsg(errorMsg)
                .setRawData(rawData);
    }

    /**
     * 创建待退款响应
     *
     * @param outRefundNo 外部退款订单号
     * @param refundNo    退款订单号
     * @param rawData     原始数据
     * @return 待退款响应
     */
    public static RefundResponse createRefundWaitingResponse(String outRefundNo, String refundNo, Object rawData) {
        return (RefundResponse) new RefundResponse()
                .setRefundStatus(RefundStatus.WAITING)
                .setOutRefundNo(outRefundNo)
                .setRefundNo(refundNo)
                .setRawData(rawData);
    }

    /**
     * 创建退款成功响应
     *
     * @param outRefundNo 外部退款订单号
     * @param refundNo    退款订单号
     * @param rawData     原始数据
     * @param successTime 成功时间
     * @return 退款成功响应
     */
    public static RefundResponse createRefundSuccessResponse(String outRefundNo, String refundNo, Object rawData, Date successTime) {
        return (RefundResponse) new RefundResponse()
                .setRefundStatus(RefundStatus.SUCCESS)
                .setOutRefundNo(outRefundNo)
                .setRefundNo(refundNo)
                .setRawData(rawData)
                .setSuccessTime(successTime);
    }

}
