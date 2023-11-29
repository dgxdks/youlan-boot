package com.youlan.plugin.pay.client;

import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.request.PayQueryRequest;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.request.RefundQueryRequest;
import com.youlan.plugin.pay.entity.request.RefundRequest;
import com.youlan.plugin.pay.entity.response.*;
import com.youlan.plugin.pay.params.PayParams;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
public abstract class AbstractPayClient<Params extends PayParams> implements PayClient {

    /**
     * 支付配置
     */
    protected final PayConfig payConfig;

    /**
     * 支付参数
     */
    protected final Params payParams;


    public AbstractPayClient(PayConfig payConfig, Params payParams) {
        this.payConfig = payConfig;
        this.payParams = payParams;
    }

    /**
     * 发起支付
     *
     * @param payRequest 支付请求
     * @return 支付响应
     * @throws Exception 异常信息
     */
    protected abstract PayResponse doPay(PayRequest payRequest) throws Exception;

    /**
     * 发起支付查询
     *
     * @param payQueryRequest 支付查询请求
     * @return 支付查询响应
     * @throws Exception 异常信息
     */
    protected abstract PayQueryResponse doPayQuery(PayQueryRequest payQueryRequest) throws Exception;

    /**
     * 发起支付回调解析
     *
     * @param params 路径参数
     * @param body   请求体
     * @return 支付回调响应
     * @throws Exception 异常信息
     */
    protected abstract PayNotifyResponse doPayNotifyParse(Map<String, String> params, String body) throws Exception;

    /**
     * 发起退款
     *
     * @param refundRequest 退款请求
     * @return 退款响应
     * @throws Exception 异常信息
     */
    protected abstract RefundResponse doRefund(RefundRequest refundRequest) throws Exception;

    /**
     * 发起退款查询
     *
     * @param refundQueryRequest 退款查询请求
     * @return 退款查询响应
     * @throws Exception 异常信息
     */
    protected abstract RefundQueryResponse doRefundQuery(RefundQueryRequest refundQueryRequest) throws Exception;

    /**
     * 发起退款回调解析
     *
     * @param params 路径参数
     * @param body   请求体
     * @return 退款回调响应
     * @throws Exception 异常信息
     */
    protected abstract RefundNotifyResponse doRefundNotifyParse(Map<String, String> params, String body) throws Exception;

    /**
     * 执行启动客户端
     */
    protected abstract void doStartClient();

    /**
     * 执行停止客户端
     */
    protected abstract void doStopClient();

    @Override
    public final PayResponse pay(PayRequest payRequest) {
        ValidatorHelper.validateWithThrow(payRequest);
        try {
            return this.doPay(payRequest);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付异常: {configId: {}, payRequest: {}}", payConfig.getId(), JSONUtil.toJsonStr(payRequest), e);
            throw ApiResultCode.E0001.getException();
        }
    }

    @Override
    public final RefundResponse refund(RefundRequest refundRequest) {
        ValidatorHelper.validateWithThrow(refundRequest);
        try {
            return this.doRefund(refundRequest);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("退款异常: {configId: {}, refundRequest: {}}", payConfig.getId(), JSONUtil.toJsonStr(refundRequest), e);
            throw ApiResultCode.E0002.getException();
        }
    }

    @Override
    public final PayQueryResponse payQuery(PayQueryRequest payQueryRequest) {
        ValidatorHelper.validateWithThrow(payQueryRequest);
        try {
            return doPayQuery(payQueryRequest);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付查询异常: {configId: {}, payQueryRequest: {}}", payConfig.getId(), JSONUtil.toJsonStr(payQueryRequest), e);
            throw ApiResultCode.E0003.getException();
        }
    }

    @Override
    public final PayNotifyResponse payNotifyParse(Map<String, String> params, String body) {
        try {
            return doPayNotifyParse(params, body);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付回调解析调异常: {configId: {}, params: {}, body: {}}", payConfig.getId(), params, body);
            throw ApiResultCode.E0004.getException();
        }
    }

    @Override
    public final RefundQueryResponse refundQuery(RefundQueryRequest refundQueryRequest) {
        ValidatorHelper.validateWithThrow(refundQueryRequest);
        try {
            return doRefundQuery(refundQueryRequest);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("退款查询异常: {configId: {}, refundQueryRequest: {}}", payConfig.getId(), JSONUtil.toJsonStr(refundQueryRequest), e);
            throw ApiResultCode.E0005.getException();
        }
    }

    @Override
    public final RefundNotifyResponse refundNotifyParse(Map<String, String> params, String body) {
        try {
            return doRefundNotifyParse(params, body);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("退款回调解析调异常: {configId: {}, params: {}, body: {}}", payConfig.getId(), params, body);
            throw ApiResultCode.E0006.getException();
        }
    }

    @Override
    public final void startClient() {
        log.info("启动支付客户端: {configId: {}, tradeType: {}}", payConfig.getId(), tradeType());
        doStartClient();
    }

    @Override
    public void stopClient() {
        log.info("停止支付客户端: {configId: {}, tradeType: {}}", payConfig.getId(), tradeType());
        this.doStopClient();
    }

    @Override
    public PayConfig payConfig() {
        return payConfig;
    }

}
