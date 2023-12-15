package com.youlan.plugin.pay.service.biz;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.plugin.pay.client.PayClient;
import com.youlan.plugin.pay.convert.PayOrderConvert;
import com.youlan.plugin.pay.entity.PayChannel;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.PayRecord;
import com.youlan.plugin.pay.entity.dto.CreatePayDTO;
import com.youlan.plugin.pay.entity.dto.SubmitPayDTO;
import com.youlan.plugin.pay.entity.request.PayQueryRequest;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.response.PayNotifyResponse;
import com.youlan.plugin.pay.entity.response.PayQueryResponse;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.entity.vo.CreatePayVO;
import com.youlan.plugin.pay.entity.vo.SubmitOrderVO;
import com.youlan.plugin.pay.enums.NotifyType;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.factory.PayClientFactory;
import com.youlan.plugin.pay.service.PayChannelService;
import com.youlan.plugin.pay.service.PayConfigService;
import com.youlan.plugin.pay.service.PayOrderService;
import com.youlan.plugin.pay.service.PayRecordService;
import com.youlan.plugin.pay.utils.PayUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class PayOrderBizService {
    private final PayOrderService payOrderService;
    private final PayRecordService payRecordService;
    private final PayChannelService payChannelService;
    private final PayConfigService payConfigService;
    private final PayClientFactory payClientFactory;
    private final PayChannelBizService payChannelBizService;
    private final PayNotifyBizService payNotifyBizService;

    /**
     * 支付创建
     *
     * @param dto 支付创建参数
     * @return 支付创建结果
     */
    public CreatePayVO createPayOrder(CreatePayDTO dto) {
        // 校验创建支付订单参数
        ValidatorHelper.validateWithThrow(dto);
        // 获取可用的支付通道
        PayChannel payChannel = this.payChannelService.loadPayChannelEnabled(dto.getChannelId());
        // 订单存在则直接返回原订单信息
        PayOrder payOrder = this.payOrderService.loadPayOrderByMchOrderId(dto.getMchOrderId());
        if (ObjectUtil.isNotNull(payOrder)) {
            log.warn("支付订单已存在：{dto: {}, payOrder:{}}", JSONUtil.toJsonStr(dto), JSONUtil.toJsonStr(payOrder));
            return PayOrderConvert.INSTANCE.convertToCreatePayOrderVO(payOrder);
        }
        // 创建支付订单
        payOrder = new PayOrder()
                .setChannelId(dto.getChannelId())
                .setPayAmount(dto.getPayAmount())
                .setExpireTime(dto.getExpireTime())
                .setMchOrderId(dto.getMchOrderId())
                .setSubject(dto.getSubject())
                .setDetail(dto.getDetail())
                .setNotifyUrl(payChannel.getPayNotifyUrl());
        this.payOrderService.save(payOrder);
        return PayOrderConvert.INSTANCE.convertToCreatePayOrderVO(payOrder);
    }

    /**
     * 支付提交
     *
     * @param dto 支付提交参数
     * @return 支付提交结果
     */
    public SubmitOrderVO submitPayOrder(SubmitPayDTO dto) {
        // 获取可以提交的支付订单
        PayOrder payOrder = this.getPayOrderCanSubmit(dto);
        // 获取可以使用的支付通道
        PayChannel payChannel = this.payChannelService.loadPayChannelEnabled(payOrder.getChannelId());
        // 获取可以使用的支付配置
        PayConfig payConfig = this.payChannelBizService.loadPayConfigEnabled(payOrder.getChannelId(), dto.getTradeType());
        // 获取支付客户端
        PayClient payClient = payClientFactory.createPayClient(payConfig, dto.getTradeType());
        // 客户端发起支付请求
        PayRequest payRequest = new PayRequest()
                .setOutTradeNo(PayUtil.generatePayOrderNo())
                .setSubject(payOrder.getSubject())
                .setDetail(payOrder.getDetail())
                .setNotifyUrl(PayUtil.generateUnifiedPayNotifyUrl(payChannel.getId(), dto.getTradeType()))
                .setReturnUrl(dto.getReturnUrl())
                .setAmount(payOrder.getPayAmount())
                .setExpireTime(payOrder.getExpireTime())
                .setClientIp(dto.getClientIp())
                .setExtraParams(dto.getExtraParams());
        PayResponse payResponse = payClient.pay(payRequest);
        // 支付响应不能是空的
        Assert.notNull(payResponse, ApiResultCode.E0001::getException);
        // 支付响应不能包含错误码和错误信息
        if (!StrUtil.isAllBlank(payResponse.getErrorCode(), payResponse.getErrorMsg())) {
            Object[] args = {payResponse.getErrorCode(), payResponse.getErrorMsg()};
            throw new BizRuntimeException(ApiResultCode.E0014, args);
        }
        // 正常发起支付请求后创建支付记录，避免创建无用的支付记录
        PayRecord payRecord = new PayRecord()
                .setOutTradeNo(payRequest.getOutTradeNo())
                .setOrderId(payOrder.getId())
                .setConfigId(payConfig.getId())
                .setTradeType(dto.getTradeType())
                .setClientIp(dto.getClientIp())
                .setPayStatus(PayStatus.WAITING)
                .setExtraParams(dto.getExtraParams())
                .setRawData(payResponse.getRawData())
                .setNotifyUrl(payRequest.getNotifyUrl());
        this.payRecordService.save(payRecord);
        // 根据支付状态处理对应逻辑
        PayStatus payStatus = payResponse.getPayStatus();
        switch (payStatus) {
            case SUCCESS:
                // 已支付处理逻辑
                getSelf().doPayOrderSuccess(payOrder, payRecord, payResponse);
                break;
            case CLOSED:
                // 已关闭处理逻辑
                getSelf().doPayOrderClosed(payRecord.getId(), payResponse);
                break;
            default:
                // 其他状态不处理
                break;
        }
        // 获取订单最新数据
        payOrder = this.payOrderService.loadPayOrderNotNull(payOrder.getId());
        return new SubmitOrderVO()
                .setPayStatus(payOrder.getPayStatus())
                .setPayShowType(payResponse.getPayShowType())
                .setPayInfo(payResponse.getPayInfo());
    }

    /**
     * 支付回调
     *
     * @param configId  支付配置ID
     * @param tradeType 交易类型
     * @param params    请求参数
     * @param body      请求体
     */
    public void payNotify(Long configId, TradeType tradeType, Map<String, String> params, String body) {
        log.info("支付回调：{configId: {}, tradeType: {}, params: {}, body: {}}", configId, tradeType,
                JSONUtil.toJsonStr(params), body);
        PayConfig payConfig = payConfigService.loadPayConfigNotNull(configId);
        PayClient payClient = payClientFactory.createPayClient(payConfig, tradeType);
        PayNotifyResponse payNotifyResponse = payClient.payNotifyParse(params, body);
        PayStatus payStatus = payNotifyResponse.getPayStatus();
        String outTradeNo = payNotifyResponse.getOutTradeNo();
        // 获取支付记录
        PayRecord payRecord = payRecordService.loadPayRecordByOutTradeNoNotNull(outTradeNo);
        switch (payStatus) {
            // 已支付处理逻辑
            case SUCCESS:
                PayOrder payOrder = payOrderService.loadPayOrderNotNull(payRecord.getOrderId());
                getSelf().doPayOrderSuccess(payOrder, payRecord, payNotifyResponse);
                break;
            // 已关闭处理逻辑
            case CLOSED:
                // 已关闭处理逻辑
                getSelf().doPayOrderClosed(payRecord.getId(), payNotifyResponse);
                break;
            default:
                log.info("支付回调不支持此支付状态：{configId: {}, tradeType: {}, params: {}, body: {}}",
                        configId, tradeType, JSONUtil.toJsonStr(params), body);
        }
    }

    /**
     * 退款回调
     *
     * @param configId  支付配置ID
     * @param tradeType 交易类型
     * @param params    请求参数
     * @param body      请求体
     */
    public void refundNotify(Long configId, TradeType tradeType, Map<String, String> params, String body) {

    }

    /**
     * 获取可以提交的支付订单
     *
     * @param dto 支付订单提交参数
     * @return 支付订单
     */
    private PayOrder getPayOrderCanSubmit(SubmitPayDTO dto) {
        // 提交参数校验
        ValidatorHelper.validateWithThrow(dto);
        // 查询支付订单
        PayOrder payOrder = this.payOrderService.loadPayOrderNotNull(dto.getOrderId());
        // 订单不能是成功状态
        if (payOrder.getPayStatus().isSuccess()) {
            throw new BizRuntimeException(ApiResultCode.E0008);
        }
        // 订单必须是待支付状态
        if (!payOrder.getPayStatus().isWaiting()) {
            throw new BizRuntimeException(ApiResultCode.E0009);
        }
        // 获取支付订单相关的支付记录列表
        List<PayRecord> payRecordList = this.payRecordService.getPayRecordListByOrderId(payOrder.getId());
        payRecordList.forEach(payRecord -> {
            // 是否存在已支付的记录
            if (payRecord.getPayStatus().isSuccess()) {
                log.warn("支付订单存在已支付记录: {orderId: {}, recordId: {}}", payOrder.getId(), payRecord.getId());
                throw new BizRuntimeException(ApiResultCode.E0011);
            }
            // 查询订单真实支付状态
            PayConfig payConfig = payConfigService.getById(payRecord.getConfigId());
            PayClient payClient = payClientFactory.createPayClient(payConfig, payRecord.getTradeType());
            PayQueryRequest payQueryRequest = new PayQueryRequest().setOutTradeNo(payRecord.getOutTradeNo());
            PayQueryResponse payQueryResponse = payClient.payQuery(payQueryRequest);
            // 存在支付成功的支付记录则不能提交
            if (payQueryResponse.getPayStatus().isSuccess()) {
                throw new BizRuntimeException(ApiResultCode.E0011);
            }
        });
        return payOrder;
    }

    /**
     * 更新支付订单为已支付状态
     *
     * @param payOrder    支付订单
     * @param payRecord   支付记录
     * @param payResponse 支付回调响应
     */
    @Transactional(rollbackFor = Exception.class)
    public void doPayOrderSuccess(PayOrder payOrder, PayRecord payRecord, PayResponse payResponse) {
        // 创建更新支付记录
        PayRecord updatePayRecord = new PayRecord()
                .setPayStatus(PayStatus.SUCCESS)
                .setTradeNo(payResponse.getTradeNo());
        // 根据支付响应类型设置原始数据
        if (payResponse instanceof PayNotifyResponse) {
            updatePayRecord.setNotifyRawData(payResponse);
        } else {
            updatePayRecord.setRawData(payResponse);
        }
        this.payRecordService.updatePayStatusSuccess(payRecord.getId(), updatePayRecord);
        // 创建更新支付订单
        PayOrder updatePayOrder = new PayOrder()
                .setPayStatus(PayStatus.SUCCESS)
                .setConfigId(payRecord.getConfigId())
                .setTradeType(payRecord.getTradeType())
                .setSuccessTime(payResponse.getSuccessTime())
                .setRecordId(payRecord.getId())
                .setOutTradeNo(payRecord.getOutTradeNo())
                .setTradeNo(payResponse.getTradeNo())
                .setClientId(payResponse.getClientId());
        this.payOrderService.updatePayOrderSuccess(payOrder.getId(), updatePayOrder);
        // 创建支付回调通知
        this.payNotifyBizService.createPayNotify(NotifyType.PAY, payOrder);
    }


    /**
     * 更新支付订单为已关闭状态
     *
     * @param recordId    支付记录ID
     * @param payResponse 支付响应
     */
    @Transactional(rollbackFor = Exception.class)
    public void doPayOrderClosed(Long recordId, PayResponse payResponse) {
        // 更新支付记录
        PayRecord updatePayRecord = new PayRecord()
                .setPayStatus(PayStatus.CLOSED)
                .setErrorCode(payResponse.getErrorCode())
                .setErrorMsg(payResponse.getErrorMsg());
        // 根据支付响应类型设置原始数据
        if (payResponse instanceof PayNotifyResponse) {
            updatePayRecord.setNotifyRawData(payResponse);
        } else {
            updatePayRecord.setRawData(payResponse);
        }
        this.payRecordService.updatePayStatusClosed(recordId, updatePayRecord);
    }

    /**
     * 获取当前实例
     */
    public PayOrderBizService getSelf() {
        return SpringUtil.getBean(PayOrderBizService.class);
    }
}
