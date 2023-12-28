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
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.entity.response.RefundResponse;
import com.youlan.plugin.pay.enums.ResponseSource;
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
     * @return 支付响应
     * @throws Exception 异常信息
     */
    protected abstract PayResponse doPayQuery(PayQueryRequest payQueryRequest) throws Exception;

    /**
     * 发起支付回调解析
     *
     * @param params 路径参数
     * @param body   请求体
     * @return 支付响应
     * @throws Exception 异常信息
     */
    protected abstract PayResponse doPayNotifyParse(Map<String, String> params, String body) throws Exception;

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
     * @return 退款响应
     * @throws Exception 异常信息
     */
    protected abstract RefundResponse doRefundQuery(RefundQueryRequest refundQueryRequest) throws Exception;

    /**
     * 发起退款回调解析
     *
     * @param params 路径参数
     * @param body   请求体
     * @return 退款响应
     * @throws Exception 异常信息
     */
    protected abstract RefundResponse doRefundNotifyParse(Map<String, String> params, String body) throws Exception;

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
            return (PayResponse) this.doPay(payRequest)
                    .setResponseSource(ResponseSource.PAY);
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
            return (RefundResponse) this.doRefund(refundRequest)
                    .setResponseSource(ResponseSource.REFUND);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("退款异常: {configId: {}, refundRequest: {}}", payConfig.getId(), JSONUtil.toJsonStr(refundRequest), e);
            throw ApiResultCode.E0002.getException();
        }
    }

    @Override
    public final PayResponse payQuery(PayQueryRequest payQueryRequest) {
        ValidatorHelper.validateWithThrow(payQueryRequest);
        try {
            return (PayResponse) doPayQuery(payQueryRequest)
                    .setResponseSource(ResponseSource.PAY_QUERY);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付查询异常: {configId: {}, payQueryRequest: {}}", payConfig.getId(), JSONUtil.toJsonStr(payQueryRequest), e);
            throw ApiResultCode.E0003.getException();
        }
    }

    @Override
    public final PayResponse payNotifyParse(Map<String, String> params, String body) {
        try {
            return (PayResponse) doPayNotifyParse(params, body)
                    .setResponseSource(ResponseSource.PAY_NOTIFY);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付回调解析调异常: {configId: {}, params: {}, body: {}}", payConfig.getId(), params, body);
            throw ApiResultCode.E0004.getException();
        }
    }

    @Override
    public final RefundResponse refundQuery(RefundQueryRequest refundQueryRequest) {
        ValidatorHelper.validateWithThrow(refundQueryRequest);
        try {
            return (RefundResponse) doRefundQuery(refundQueryRequest)
                    .setResponseSource(ResponseSource.REFUND_QUERY);
        } catch (BizRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("退款查询异常: {configId: {}, refundQueryRequest: {}}", payConfig.getId(), JSONUtil.toJsonStr(refundQueryRequest), e);
            throw ApiResultCode.E0005.getException();
        }
    }

    @Override
    public final RefundResponse refundNotifyParse(Map<String, String> params, String body) {
        try {
            return (RefundResponse) doRefundNotifyParse(params, body)
                    .setResponseSource(ResponseSource.REFUND_NOTIFY);
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
