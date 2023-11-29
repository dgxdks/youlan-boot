package com.youlan.plugin.pay.service.biz;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.plugin.pay.client.PayClient;
import com.youlan.plugin.pay.convert.PayClientConvert;
import com.youlan.plugin.pay.convert.PayOrderConvert;
import com.youlan.plugin.pay.entity.PayChannel;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.PayRecord;
import com.youlan.plugin.pay.entity.dto.CreatePayOrderDTO;
import com.youlan.plugin.pay.entity.dto.SubmitPayOrderDTO;
import com.youlan.plugin.pay.entity.request.PayQueryRequest;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.response.PayQueryResponse;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.entity.vo.CreatePayOrderVO;
import com.youlan.plugin.pay.entity.vo.SubmitPayOrderVO;
import com.youlan.plugin.pay.enums.PayStatus;
import com.youlan.plugin.pay.factory.PayClientFactory;
import com.youlan.plugin.pay.params.PayParams;
import com.youlan.plugin.pay.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PayBizService {
    private final PayConfigService payConfigService;
    private final PayChannelService payChannelService;
    private final PayChannelConfigService payChannelConfigService;
    private final PayOrderService payOrderService;
    private final PayRecordService payRecordService;
    private final PayClientFactory payClientFactory;

    /**
     * 创建支付订单
     *
     * @param dto 创建支付订单参数
     * @return 支付订单
     */
    public CreatePayOrderVO createPayOrder(CreatePayOrderDTO dto) {
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
     * 提交支付订单
     *
     * @param dto 提交支付订单参数
     * @return 支付订单
     */
    public SubmitPayOrderVO submitPayOrder(SubmitPayOrderDTO dto) {
        // 获取可以提交的支付订单
        PayOrder payOrder = this.getPayOrderCanSubmit(dto);
        // 获取可以使用的支付通道
        PayChannel payChannel = this.payChannelService.loadPayChannelEnabled(dto.getChannelId());
        // 获取可以使用的支付配置
        PayConfig payConfig = this.payChannelService.loadPayConfigEnabled(dto.getChannelId(), dto.getTradeType());
        // 获取支付客户端
        PayClient payClient = payClientFactory.createPayClient(payConfig, dto.getTradeType());
        // 创建支付记录
        PayRecord payRecord = this.payRecordService.createPayRecord(dto, payOrder, payConfig);
        // 客户端发起支付请求
        PayRequest payRequest = PayClientConvert.INSTANCE.convertPayRequest(dto, payOrder, payRecord, payChannel);
        PayResponse payResponse = payClient.pay(payRequest);
        // 支付响应不能是空的
        if (ObjectUtil.isNull(payResponse)) {
            throw new BizRuntimeException(ApiResultCode.E0001);
        }
        // 支付响应不能包含错误码
        if (StrUtil.isNotBlank(payResponse.getErrorCode())) {
            Object[] args = {payResponse.getErrorCode(), payResponse.getErrorMsg()};
            throw new BizRuntimeException(ApiResultCode.E0014, args);
        }
        // 根据支付状态处理对应逻辑
        PayStatus payStatus = payResponse.getPayStatus();
        // 已成功处理逻辑
        if (payStatus == PayStatus.SUCCESS) {
            this.getSelf().updatePayOrderSuccess(payOrder, payRecord, payResponse);
        }
        // 已关闭处理逻辑
        if (payStatus == PayStatus.CLOSED) {
            this.getSelf().updatePayOrderClosed(payRecord.getId(), payResponse);
        }
        // 获取订单最新数据
        payOrder = this.payOrderService.loadPayOrderIfExists(payOrder.getId());
        return new SubmitPayOrderVO()
                .setPayStatus(payOrder.getPayStatus())
                .setPayShowType(payResponse.getPayShowType())
                .setPayInfo(payResponse.getPayInfo());
    }


    @Transactional(rollbackFor = Exception.class)
    public void updatePayOrderSuccess(PayOrder payOrder, PayRecord payRecord, PayResponse payResponse) {
        // 更新支付记录
        PayRecord updatePayRecord = new PayRecord()
                .setPayStatus(PayStatus.SUCCESS)
                .setRawData(payResponse);
        this.payRecordService.updatePayStatusSuccess(payRecord.getId(), updatePayRecord);
        // 更新支付订单
        PayOrder updatePayOrder = new PayOrder()
                .setPayStatus(PayStatus.SUCCESS)
                .setConfigId(payOrder.getConfigId())
                .setTradeType(payOrder.getTradeType())
                .setSuccessTime(payResponse.getSuccessTime())
                .setRecordId(payRecord.getId())
                .setOutTradeNo(payRecord.getOutTradeNo())
                .setTradeNo(payOrder.getTradeNo())
                .setClientId(payResponse.getClientId());
        this.payOrderService.updatePayOrderSuccess(payOrder.getId(), updatePayOrder);
    }

    /**
     * 更新支付订单为已关闭状态
     *
     * @param recordId    支付记录ID
     * @param payResponse 支付响应
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePayOrderClosed(Long recordId, PayResponse payResponse) {
        // 更新支付记录
        PayRecord updatePayRecord = new PayRecord()
                .setPayStatus(PayStatus.CLOSED)
                .setRawData(JSONUtil.toJsonStr(payResponse))
                .setErrorCode(payResponse.getErrorCode())
                .setErrorMsg(payResponse.getErrorMsg());
        this.payRecordService.updatePayStatusClosed(recordId, updatePayRecord);
    }

    /**
     * 获取可以提交的支付订单
     *
     * @param dto 支付订单提交参数
     * @return 支付订单
     */
    private PayOrder getPayOrderCanSubmit(SubmitPayOrderDTO dto) {
        // 提交参数校验
        ValidatorHelper.validateWithThrow(dto);
        // 查询支付订单
        PayOrder payOrder = this.payOrderService.loadPayOrderIfExists(dto.getOrderId());
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
     * 获取当前业务对象
     *
     * @return 当前业务对象
     */
    public PayBizService getSelf() {
        return SpringUtil.getBean(PayBizService.class);
    }
}
