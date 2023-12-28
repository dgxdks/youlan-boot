package com.youlan.plugin.pay.client.wx;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.request.WxPayMicropayRequest;
import com.github.binarywang.wxpay.bean.result.WxPayMicropayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.youlan.plugin.pay.constant.PayConstant;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.enums.PayShowType;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.params.WxPayParams;
import com.youlan.plugin.pay.utils.PayClientUtil;
import com.youlan.plugin.pay.utils.WxPayUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WxScanPayClient extends AbstractWxPayClient {
    /**
     * 授权码过期时间(s)
     */
    private static final int AUTH_CODE_EXPIRE_SECONDS = 3 * 60;
    /**
     * 付款码支付重试间隔(s)
     */
    private static final int MICRO_PAY_RETRY_INTERVAL = 5;
    /**
     * 付款码支付重试次数
     */
    private static final int MICRO_PAY_RETRY_TIMES = 32;

    public WxScanPayClient(PayConfig payConfig, WxPayParams payParams) {
        super(payConfig, payParams);
    }

    @Override
    protected PayResponse doPayV2(PayRequest payRequest) throws WxPayException {
        // 获取授权码
        String authCode = payRequest.getExtraParamNotNull(PayConstant.PARAM_KEY_AUTH_CODE);
        // 创建默认过期时间
        Date expireTime = DateUtil.offsetSecond(new Date(), AUTH_CODE_EXPIRE_SECONDS);
        // 如果默认过期时间晚于请求过期时间，则使用请求过期时间
        if (ObjectUtil.isNotNull(payRequest.getExpireTime()) && DateUtil.compare(expireTime, payRequest.getExpireTime()) > 0) {
            expireTime = payRequest.getExpireTime();
        }
        // 创建V2请求
        WxPayMicropayRequest orderRequest = WxPayMicropayRequest.newBuilder()
                .outTradeNo(payRequest.getOutTradeNo())
                .body(payRequest.getSubject())
                .detail(payRequest.getDetail())
                .totalFee(WxPayUtil.formatFee(payRequest.getAmount()))
                .timeExpire(WxPayUtil.formatTimeV2(expireTime))
                .spbillCreateIp(payRequest.getClientIp())
                .authCode(authCode)
                .build();
        // 创建支付订单
        WxPayException lastException = null;
        for (int i = 0; i < MICRO_PAY_RETRY_TIMES; i++) {
            try {
                // 付款码支付结果
                WxPayMicropayResult orderResult = wxPayService.micropay(orderRequest);
                return PayClientUtil.createPaySuccessResponse(orderResult.getOutTradeNo(), orderResult.getTransactionId(),
                        orderResult.getOpenid(), orderResult, PayShowType.BAR_CODE, WxPayUtil.parseTimeV2(orderResult.getTimeEnd()));
            } catch (WxPayException e) {
                lastException = e;
                // 非以下响应直接抛出异常
                if (!StrUtil.equalsAny(e.getErrCode(), "SYSTEMERROR", "USERPAYING", "BANKERROR")) {
                    throw e;
                }
                log.info("付款码支付失败：{重试次数: {}, 重试间隔: {}, 请求参数: {}, 响应结果: {}}", i, MICRO_PAY_RETRY_INTERVAL, JSONUtil.toJsonStr(orderRequest), WxPayUtil.getErrorMsg(e));
                ThreadUtil.sleep(MICRO_PAY_RETRY_INTERVAL, TimeUnit.SECONDS);
            }
        }
        throw lastException;
    }

    @Override
    protected PayResponse doPayV3(PayRequest payRequest) throws WxPayException {
        return this.doPayV2(payRequest);
    }

    @Override
    public TradeType tradeType() {
        return TradeType.WX_SCAN;
    }
}
