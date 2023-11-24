package com.youlan.plugin.pay.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.FileHelper;
import com.youlan.plugin.pay.constant.PayConstant;
import com.youlan.plugin.pay.entity.response.*;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.helper.MoneyHelper;
import com.youlan.plugin.pay.params.WxPayParams;

import java.math.BigDecimal;
import java.util.Date;

import static cn.hutool.core.date.DatePattern.PURE_DATETIME_PATTERN;
import static cn.hutool.core.date.DatePattern.UTC_WITH_XXX_OFFSET_PATTERN;
import static com.youlan.plugin.pay.constant.PayConstant.*;

public class WxPayUtil {

    /**
     * 创建退款通知响应
     *
     * @param refundStatus 退款状态
     * @param outRefundNo  外部退款订单号
     * @param refundNo     退款订单号
     * @param rawData      原始数据
     * @param successTime  成功时间
     * @return 退款通知响应
     */
    public static RefundNotifyResponse createRefundNotifyResponse(String refundStatus, String outRefundNo, String refundNo,
                                                                  Object rawData, Date successTime) {
        return (RefundNotifyResponse) new RefundNotifyResponse()
                .setRefundStatus(refundStatus)
                .setOutRefundNo(outRefundNo)
                .setRefundNo(refundNo)
                .setRawData(rawData)
                .setSuccessTime(successTime);
    }

    /**
     * 创建退款查询响应
     *
     * @param refundStatus 退款状态
     * @param outRefundNo  外部退款订单号
     * @param refundNo     退款订单号
     * @param rawData      原始数据
     * @param successTime  成功时间
     * @return 退款查询响应
     */
    public static RefundQueryResponse createRefundQueryResponse(String refundStatus, String outRefundNo, String refundNo,
                                                                Object rawData, Date successTime) {
        return (RefundQueryResponse) new RefundQueryResponse()
                .setRefundStatus(refundStatus)
                .setOutRefundNo(outRefundNo)
                .setRefundNo(refundNo)
                .setRawData(rawData)
                .setSuccessTime(successTime);
    }

    /**
     * 创建支付回调响应
     *
     * @param payStatus   支付状态
     * @param tradeNo     交易号
     * @param userId      用户ID
     * @param rwaData     原始数据
     * @param successTime 成功时间
     * @return 支付回调响应
     */
    public static PayNotifyResponse createPayNotifyResponse(String payStatus, String tradeNo, String userId, Object rwaData,
                                                            Date successTime) {
        return (PayNotifyResponse) new PayNotifyResponse()
                .setPayStatus(payStatus)
                .setTradeNo(tradeNo)
                .setClientId(userId)
                .setRawData(rwaData)
                .setSuccessTime(successTime);
    }

    /**
     * 创建支付查询响应
     *
     * @param payStatus   支付状态
     * @param tradeNo     交易号
     * @param userId      用户ID
     * @param rwaData     原始数据
     * @param successTime 成功时间
     * @return 支付查询响应
     */
    public static PayQueryResponse createPayQueryResponse(String payStatus, String tradeNo, String userId, Object rwaData,
                                                          Date successTime) {
        return (PayNotifyResponse) new PayNotifyResponse()
                .setPayStatus(payStatus)
                .setTradeNo(tradeNo)
                .setClientId(userId)
                .setRawData(rwaData)
                .setSuccessTime(successTime);
    }

    /**
     * 创建待支付响应
     *
     * @param outTradeNo 外部交易订单号
     * @param rawData    原始数据
     * @return 待支付响应
     */
    public static PayResponse createPayWaitingResponse(String outTradeNo, Object rawData) {
        return createPayResponse(PayConstant.PAY_STATUS_WAITING, outTradeNo, rawData, null, null);
    }

    /**
     * 创建支付失败响应
     *
     * @param outTradeNo     外部订单号
     * @param wxPayException 微信支付异常
     * @return 失败响应
     */
    public static PayResponse createPayFailedResponse(String outTradeNo, WxPayException wxPayException) {
        return createPayResponse(PayConstant.PAY_STATUS_FAILED, outTradeNo, wxPayException,
                getErrorCode(wxPayException), getErrorMsg(wxPayException));
    }

    /**
     * 创建支付响应
     *
     * @param payStatus  支付状态
     * @param outTradeNo 外部交易订单号
     * @param rawData    原始数据
     * @param errorCode  错误编码
     * @param errorMsg   错误信息
     * @return 支付响应
     */
    public static PayResponse createPayResponse(String payStatus, String outTradeNo, Object rawData, String errorCode,
                                                String errorMsg) {
        return (PayResponse) new PayResponse()
                .setPayStatus(payStatus)
                .setOutTradeNo(outTradeNo)
                .setRawData(rawData)
                .setErrorCode(errorCode)
                .setErrorMsg(errorMsg);
    }

    /**
     * 创建退款失败响应
     *
     * @param outRefundNo    外部退款号
     * @param wxPayException 微信支付异常
     * @return 失败响应
     */
    public static RefundResponse createRefundFailedResponse(String outRefundNo, WxPayException wxPayException) {
        return createRefundResponse(REFUND_STATUS_FAILED, outRefundNo, null, wxPayException,
                getErrorCode(wxPayException), getErrorMsg(wxPayException), null);
    }

    /**
     * 创建退款失败响应
     *
     * @param outRefundNo 外部退款号
     * @param rawData     原始数据
     * @return 失败响应
     */
    public static RefundResponse createRefundFailedResponse(String outRefundNo, Object rawData) {
        return createRefundResponse(REFUND_STATUS_FAILED, outRefundNo, null, rawData,
                null, null, null);
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
        return createRefundResponse(REFUND_STATUS_WAITING, outRefundNo, refundNo, rawData, null, null, null);
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
        return createRefundResponse(REFUND_STATUS_SUCCESS, outRefundNo, refundNo, rawData, null, null, successTime);
    }

    /**
     * 创建退款响应
     *
     * @param refundStatus 退款状态
     * @param outRefundNo  外部退款订单号
     * @param refundNo     退款订单号
     * @param rawData      原始数据
     * @param errorCode    错误编码
     * @param errorMsg     错误信息
     * @param successTime  成功时间
     * @return 退款响应
     */
    public static RefundResponse createRefundResponse(String refundStatus, String outRefundNo, String refundNo,
                                                      Object rawData, String errorCode, String errorMsg, Date successTime) {
        return (RefundResponse) new RefundResponse()
                .setRefundStatus(refundStatus)
                .setOutRefundNo(outRefundNo)
                .setRefundNo(refundNo)
                .setRawData(rawData)
                .setErrorCode(errorCode)
                .setErrorMsg(errorMsg)
                .setSuccessTime(successTime);
    }

    /**
     * 获取错误码
     *
     * @param wxPayException 微信支付异常
     * @return 错误码
     */
    public static String getErrorCode(WxPayException wxPayException) {
        if (StrUtil.isNotBlank(wxPayException.getErrCode())) {
            return wxPayException.getErrCode();
        }
        return wxPayException.getReturnCode();
    }

    /**
     * 获取错误信息
     *
     * @param wxPayException 微信支付异常
     * @return 错误信息
     */
    public static String getErrorMsg(WxPayException wxPayException) {
        if (StrUtil.isNotBlank(wxPayException.getErrCode())) {
            return wxPayException.getErrCodeDes();
        }
        if (StrUtil.isNotBlank(wxPayException.getCustomErrorMsg())) {
            return wxPayException.getCustomErrorMsg();
        }
        return wxPayException.getReturnMsg();
    }

    /**
     * 交易状态转支付状态
     * {@link WxPayOrderQueryResult#getTradeState()}
     *
     * @param tradeState 交易状态
     * @return 支付状态
     */
    public static PayStatus tradeStateToPayStatus(String tradeState) {
        switch (tradeState) {
            case "SUCCESS":
                return PayStatus.SUCCESS;
            case "REFUND":
                return PayStatus.REFUND;
            case "NOTPAY":
            case "USERPAYING":
                return PayStatus.WAITING;
            case "CLOSED":
            case "REVOKED":
            case "PAYERROR":
                return PayStatus.FAILED;
            default:
                throw new BizRuntimeException(StrUtil.format("未知的交易状态:{}", tradeState));
        }
    }

    /**
     * 格式化金额
     *
     * @param amount 金额(元)
     * @return 格式化金额
     */
    public static int formatFee(BigDecimal amount) {
        BigDecimal fenAmount = MoneyHelper.yuanToFen(amount);
        return fenAmount.intValue();
    }

    /**
     * 格式化支付金额
     *
     * @param amount 金额(元)
     * @return 格式化金额
     */
    public static WxPayUnifiedOrderV3Request.Amount formatPayAmount(BigDecimal amount) {
        int totalFee = formatFee(amount);
        return new WxPayUnifiedOrderV3Request.Amount()
                .setTotal(totalFee)
                .setCurrency(CURRENCY_CNY);
    }

    // 格式化退款金额
    public static WxPayRefundV3Request.Amount formatRefundAmount(BigDecimal amount) {
        int totalFee = formatFee(amount);
        return new WxPayRefundV3Request.Amount()
                .setTotal(totalFee)
                .setCurrency(CURRENCY_CNY);
    }


    /**
     * 格式化过期时间V2
     *
     * @param expireTime 过期时间
     * @return 格式化时间
     */
    public static String formatExpireTimeV2(Date expireTime) {
        return DateUtil.format(expireTime, PURE_DATETIME_PATTERN);
    }

    /**
     * 解析过期时间V2
     *
     * @param expireTime 过期时间
     * @return 解析时间
     */
    public static Date parseExpireTimeV2(String expireTime) {
        return DateUtil.parse(expireTime, PURE_DATETIME_PATTERN);
    }

    /**
     * 格式化过期时间V3
     *
     * @param expireTime 过期时间
     * @return 格式化时间
     */
    public static String formatExpireTimeV3(Date expireTime) {
        return DateUtil.format(expireTime, UTC_WITH_XXX_OFFSET_PATTERN);
    }

    /**
     * 解析过期时间V3
     *
     * @param expireTime 过期时间
     * @return 解析时间
     */
    public static Date parseExpireTimeV3(String expireTime) {
        return DateUtil.parse(expireTime, UTC_WITH_XXX_OFFSET_PATTERN);
    }

    /**
     * 创建微信支付配置
     *
     * @param wxPayParams 微信支付参数
     * @return 微信支付配置
     */
    public static WxPayConfig createWxPayconfig(WxPayParams wxPayParams, TradeType tradeType) {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wxPayParams.getAppId());
        wxPayConfig.setMchId(wxPayConfig.getMchId());
        wxPayConfig.setMchKey(wxPayConfig.getMchKey());
        wxPayConfig.setSubAppId(wxPayParams.getSubAppId());
        wxPayConfig.setSubMchId(wxPayParams.getSubMchId());
        // apiclient_cert.p12证书存储格式为Base64
        String keyContent = wxPayParams.getKeyContent();
        if (StrUtil.isNotBlank(keyContent)) {
            Assert.isTrue(Base64.isBase64(keyContent), () -> new BizRuntimeException("apiclient_cert.p12格式错误"));
            byte[] decodeKeyContent = Base64.decode(keyContent);
            wxPayConfig.setKeyPath(FileHelper.createTempFile(decodeKeyContent).getAbsolutePath());
        }
        // apiclient_key.pem证书存储格式为明文
        String privateKeyContent = wxPayParams.getPrivateKeyContent();
        if (StrUtil.isNotBlank(privateKeyContent)) {
            wxPayConfig.setPrivateKeyPath(FileHelper.createTempFile(privateKeyContent).getAbsolutePath());
        }
        // apiclient_cert.pem证书存储格式为明文
        String privateCertContent = wxPayParams.getPrivateCertContent();
        if (StrUtil.isNotBlank(privateCertContent)) {
            wxPayConfig.setPrivateCertPath(FileHelper.createTempFile(privateCertContent).getAbsolutePath());
        }
        switch (tradeType) {
            case WX_JSAPI:
            case WX_MINI:
                wxPayConfig.setTradeType(WxPayConstants.TradeType.JSAPI);
                break;
            case WX_APP:
                wxPayConfig.setTradeType(WxPayConstants.TradeType.APP);
                break;
            case WX_H5:
                wxPayConfig.setTradeType(WxPayConstants.TradeType.MWEB);
                break;
            case WX_NATIVE:
                wxPayConfig.setTradeType(WxPayConstants.TradeType.NATIVE);
                break;
            case WX_SCAN:
                wxPayConfig.setTradeType(WxPayConstants.TradeType.MICROPAY);
                break;
            default:
                throw new BizRuntimeException("不支持的交易类型");
        }
        return wxPayConfig;
    }
}
