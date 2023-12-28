package com.youlan.plugin.pay.service.biz;

import cn.hutool.core.collection.CollectionUtil;
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
import com.youlan.plugin.pay.entity.dto.CreatePayOrderDTO;
import com.youlan.plugin.pay.entity.dto.SubmitPayOrderDTO;
import com.youlan.plugin.pay.entity.request.PayQueryRequest;
import com.youlan.plugin.pay.entity.request.PayRequest;
import com.youlan.plugin.pay.entity.response.PayResponse;
import com.youlan.plugin.pay.entity.vo.CreatePayOrderVO;
import com.youlan.plugin.pay.entity.vo.SubmitPayOrderVO;
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
    private final PayConfigBizService payConfigBizService;
    private final PayNotifyBizService payNotifyBizService;

    /**
     * 根据商户订单号获取支付订单
     *
     * @param mchOrderId 商户订单号
     * @return 支付订单
     */
    public PayOrder getPayOrderByMchOrderId(String mchOrderId) {
        return this.payOrderService.loadPayOrderByMchOrderNotNull(mchOrderId);
    }

    /**
     * 根据支付订单ID获取支付订单
     *
     * @param orderId 支付订单ID
     * @return 支付订单
     */
    public PayOrder getPayOrderByOrderId(Long orderId) {
        return this.payOrderService.loadPayOrderNotNull(orderId);
    }

    /**
     * 创建支付订单
     *
     * @param dto 创建参数
     * @return 支付订单创建结果
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
     * @param dto 提交参数
     * @return 支付订单提交结果
     */
    public SubmitPayOrderVO submitPayOrder(SubmitPayOrderDTO dto) {
        // 获取可以提交的支付订单
        PayOrder payOrder = this.getPayOrderCanSubmit(dto);
        // 获取可以使用的支付通道
        PayChannel payChannel = this.payChannelService.loadPayChannelEnabled(payOrder.getChannelId());
        // 获取可以使用的支付配置
        PayConfig payConfig = this.payConfigBizService.loadPayConfigEnabled(payOrder.getChannelId(), dto.getTradeType());
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
        // 支付响应不能包含错误码和错误信息
        if (!StrUtil.isAllBlank(payResponse.getErrorCode(), payResponse.getErrorMsg())) {
            Object[] args = {payResponse.getErrorCode(), payResponse.getErrorMsg()};
            throw new BizRuntimeException(ApiResultCode.E0014, args);
        }
        // 正常发起支付请求后创建支付记录，避免创建无用的支付记录
        PayRecord payRecord = new PayRecord()
                .setOutTradeNo(payRequest.getOutTradeNo())
                .setTradeNo(payResponse.getTradeNo())
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
                getSelf().doPayOrderSuccess(payResponse);
                break;
            case CLOSED:
                // 已关闭处理逻辑
                getSelf().doPayOrderClosed(payResponse);
                break;
            default:
                // 其他状态不处理
                break;
        }
        // 获取订单最新数据
        payOrder = this.payOrderService.loadPayOrderNotNull(payOrder.getId());
        return new SubmitPayOrderVO()
                .setPayStatus(payOrder.getPayStatus())
                .setPayShowType(payResponse.getPayShowType())
                .setPayInfo(payResponse.getPayInfo());
    }

    /**
     * 调度支付记录同步
     */
    public void scheduleSyncPayRecord() {
        // 支付订单同步其实就是同步支付记录
        List<PayRecord> payRecordList = this.payRecordService.getPayRecordCanSync();
        if (CollectionUtil.isEmpty(payRecordList)) {
            return;
        }
        int successCount = 0;
        for (PayRecord payOrder : payRecordList) {
            successCount += syncPayRecord(payOrder) ? 1 : 0;
        }
        log.info("支付记录同步调度任务: {支付记录总数: {}, 已同步: {}}", payRecordList.size(), successCount);
    }

    /**
     * 支付订单同步
     *
     * @param id 支付订单ID
     */
    public void syncPayOrder(Long id) {
        PayOrder payOrder = this.payOrderService.loadPayOrderNotNull(id);
        List<PayRecord> payRecordList = this.payRecordService.getPayRecordListByOrderId(payOrder.getId());
        if (CollectionUtil.isEmpty(payRecordList)) {
            return;
        }
        int successCount = 0;
        for (PayRecord payRecord : payRecordList) {
            successCount += syncPayRecord(payRecord) ? 1 : 0;
        }
        log.info("支付订单同步：{支付记录总数：{}, 已同步：{}}", payRecordList.size(), successCount);
    }

    /**
     * 同步支付记录
     *
     * @param payRecord 支付记录
     * @return 是否同步成功
     */
    private boolean syncPayRecord(PayRecord payRecord) {
        try {
            PayConfig payConfig = this.payConfigService.loadPayConfigNotNull(payRecord.getConfigId());
            PayClient payClient = this.payClientFactory.createPayClient(payConfig, payRecord.getTradeType());
            PayQueryRequest payQueryRequest = new PayQueryRequest()
                    .setOutTradeNo(payRecord.getOutTradeNo());
            PayResponse payResponse = payClient.payQuery(payQueryRequest);
            PayStatus payStatus = payResponse.getPayStatus();
            switch (payStatus) {
                case SUCCESS:
                    getSelf().doPayOrderSuccess(payResponse);
                    break;
                case CLOSED:
                    getSelf().doPayOrderClosed(payResponse);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("支付记录同步失败：{id: {}}", payRecord.getId());
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 调度过期支付订单
     */
    public void scheduleExpirePayOrder() {
        List<PayOrder> payOrderList = this.payOrderService.getPayOrderListCanExpire();
        if (CollectionUtil.isEmpty(payOrderList)) {
            return;
        }
        int successCount = 0;
        for (PayOrder payOrder : payOrderList) {
            successCount += expirePayOrder(payOrder) ? 1 : 0;
        }
        log.info("支付订单过期调度任务: {支付订单总数: {}, 已过期: {}}", payOrderList.size(), successCount);
    }

    /**
     * 过期支付订单
     *
     * @param payOrder 支付订单
     */
    private boolean expirePayOrder(PayOrder payOrder) {
        try {
            List<PayRecord> payRecordList = payRecordService.getPayRecordListByOrderId(payOrder.getId());
            for (PayRecord payRecord : payRecordList) {
                PayStatus payStatus = payRecord.getPayStatus();
                // 已关闭不需要处理
                if (payStatus.isClosed()) {
                    continue;
                }
                // 存在已支付不能更新为过期状态
                if (payStatus.isSuccess()) {
                    log.warn("支付订单过期失败，因为存在已支付的支付记录：{orderId: {}, recordId: {}}",
                            payOrder.getId(), payRecord.getId());
                    return false;
                }
                // 查询真实支付状态
                PayConfig payConfig = this.payConfigService.loadPayConfigNotNull(payRecord.getConfigId());
                PayClient payClient = this.payClientFactory.createPayClient(payConfig, payRecord.getTradeType());
                PayQueryRequest payQueryRequest = new PayQueryRequest()
                        .setOutTradeNo(payRecord.getOutTradeNo());
                PayResponse payResponse = payClient.payQuery(payQueryRequest);
                // 绑定真实支付状态
                payStatus = payResponse.getPayStatus();
                // 真实状态为已退款不能更新为过期状态
                if (payStatus.isRefund()) {
                    log.warn("支付订单过期失败，支付记录实际已退款：{orderId: {}, recordId: {}}",
                            payOrder.getId(), payRecord.getId());
                    return false;
                }
                // 真实状态为已支付不能更新为过期状态且需要即时更新支付订单
                if (payStatus.isSuccess()) {
                    log.warn("支付订单过期失败，支付记录实际已支付，需更新支付订单：{orderId: {}, recordId: {}}",
                            payOrder.getId(), payRecord.getId());
                    getSelf().doPayOrderSuccess(payResponse);
                    return false;
                }
                // 非以上两种状态则需要将支付记录改为已关闭
                PayRecord updatePayRecord = new PayRecord()
                        .setPayStatus(PayStatus.CLOSED)
                        .setNotifyRawData(payResponse);
                boolean update = this.payRecordService.updateByIdAndPayStatus(payRecord.getId(), PayStatus.WAITING, updatePayRecord);
                if (!update) {
                    log.warn("支付订单过期失败，支付记录无法修改为已关闭：{orderId: {}, recordId: {}}",
                            payOrder.getId(), payRecord.getId());
                    return false;
                }
                log.info("支付记录修改为已关闭成功，即将关闭支付订单：{orderId: {}, recordId: {}}",
                        payOrder.getId(), payRecord.getId());
            }
            // 以上逻辑未中断执行，可将支付订单修改为已关闭
            PayOrder updatePayOrder = new PayOrder()
                    .setPayStatus(PayStatus.CLOSED);
            boolean update = this.payOrderService.updateByIdAndPayStatus(payOrder.getId(), PayStatus.WAITING, updatePayOrder);
            if (!update) {
                log.warn("支付订单过期失败，因为支付订单无法修改为已关闭：{id: {}}", payOrder.getId());
                return false;
            }
            log.info("支付订单过期成功，已修改为已关闭成功：{id: {}}", payOrder.getId());
            return true;
        } catch (Exception e) {
            log.error("支付订单过期异常：{id: {}}", payOrder.getId());
            log.error(e.getMessage(), e);
            return false;
        }
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
        PayResponse payResponse = payClient.payNotifyParse(params, body);
        PayStatus payStatus = payResponse.getPayStatus();
        switch (payStatus) {
            // 已支付处理逻辑
            case SUCCESS:
                getSelf().doPayOrderSuccess(payResponse);
                break;
            // 已关闭处理逻辑
            case CLOSED:
                // 已关闭处理逻辑
                getSelf().doPayOrderClosed(payResponse);
                break;
            default:
                log.info("支付回调不支持此支付状态：{configId: {}, tradeType: {}, params: {}, body: {}}",
                        configId, tradeType, JSONUtil.toJsonStr(params), body);
        }
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
            PayResponse payResponse = payClient.payQuery(payQueryRequest);
            // 存在支付成功的支付记录则不能提交
            if (payResponse.getPayStatus().isSuccess()) {
                throw new BizRuntimeException(ApiResultCode.E0011);
            }
        });
        return payOrder;
    }

    /**
     * 更新支付订单为已支付状态
     *
     * @param payResponse 支付响应
     */
    @Transactional(rollbackFor = Exception.class)
    public void doPayOrderSuccess(PayResponse payResponse) {
        // 获取支付记录
        PayRecord payRecord = payRecordService.loadPayRecordByOutTradeNoNotNull(payResponse.getOutTradeNo());
        PayStatus payStatus = payRecord.getPayStatus();
        // 待支付需要更新支付记录，已支付不需要更新，其他状态中断执行
        switch (payStatus) {
            case WAITING:
                // 创建更新支付记录
                PayRecord updatePayRecord = new PayRecord()
                        .setPayStatus(PayStatus.SUCCESS)
                        .setTradeNo(payResponse.getTradeNo());
                // 根据响应来源设置原始数据
                if (payResponse.getResponseSource().isPayNotify()) {
                    updatePayRecord.setNotifyRawData(payResponse);
                } else {
                    updatePayRecord.setRawData(payResponse);
                }
                boolean update = payRecordService.updateByIdAndPayStatus(payRecord.getId(), PayStatus.WAITING, updatePayRecord);
                if (!update) {
                    throw new BizRuntimeException(ApiResultCode.E0016);
                }
                log.info("支付记录更新为已支付状态：{id: {}}", payRecord.getId());
                break;
            case SUCCESS:
                log.info("支付记录是已支付状态，无需更新：{id: {}}", payRecord.getId());
                break;
            default:
                throw new BizRuntimeException(ApiResultCode.E0016);
        }
        // 获取支付订单
        PayOrder payOrder = payOrderService.loadPayOrderNotNull(payRecord.getOrderId());
        payStatus = payOrder.getPayStatus();
        // 待支付需要更新支付订单，已支付不需要更新，其他状态中断执行
        switch (payStatus) {
            case SUCCESS:
                log.info("支付订单是已支付状态，无需更新：{id: {}}", payOrder.getId());
                break;
            case WAITING:
                // 创建更新支付订单
                PayOrder updatePayOrder = new PayOrder()
                        .setPayStatus(PayStatus.SUCCESS)
                        .setConfigId(payRecord.getConfigId())
                        .setTradeType(payRecord.getTradeType())
                        .setSuccessTime(payResponse.getSuccessTime())
                        .setRecordId(payRecord.getId())
                        .setOutTradeNo(payRecord.getOutTradeNo())
                        .setTradeNo(payResponse.getTradeNo())
                        .setClientId(payResponse.getClientId())
                        .setClientIp(payRecord.getClientIp());
                boolean update = this.payOrderService.updateByIdAndPayStatus(payOrder.getId(), PayStatus.WAITING, updatePayOrder);
                if (!update) {
                    throw new BizRuntimeException(ApiResultCode.E0009);
                }
                log.info("支付订单更新为已支付状态：{id: {}}", payOrder.getId());
                // 创建支付回调通知
                this.payNotifyBizService.createPayNotify(NotifyType.PAY, payOrder.getId(), payOrder.getNotifyUrl());
                break;
            default:
                throw new BizRuntimeException(ApiResultCode.E0009);
        }
    }


    /**
     * 更新支付订单为已关闭状态
     *
     * @param payResponse 支付响应
     */
    @Transactional(rollbackFor = Exception.class)
    public void doPayOrderClosed(PayResponse payResponse) {
        // 获取支付记录
        PayRecord payRecord = payRecordService.loadPayRecordByOutTradeNoNotNull(payResponse.getOutTradeNo());
        PayStatus payStatus = payRecord.getPayStatus();
        switch (payStatus) {
            case CLOSED:
                log.info("支付记录是已关闭状态，无需更新：{id: {}}", payRecord.getId());
                return;
            case SUCCESS:
                log.info("支付记录是已成功状态，无需更新：{id: {}}", payRecord.getId());
                break;
            case WAITING:
                // 更新支付记录
                PayRecord updatePayRecord = new PayRecord()
                        .setPayStatus(PayStatus.CLOSED)
                        .setErrorCode(payResponse.getErrorCode())
                        .setErrorMsg(payResponse.getErrorMsg());
                // 根据响应来源设置原始数据
                if (payResponse.getResponseSource().isPayNotify()) {
                    updatePayRecord.setNotifyRawData(payResponse);
                } else {
                    updatePayRecord.setRawData(payResponse);
                }
                this.payRecordService.updateByIdAndPayStatus(payRecord.getId(), PayStatus.CLOSED, updatePayRecord);
            default:
                throw new BizRuntimeException(ApiResultCode.E0016);
        }
    }

    /**
     * 删除支付订单
     *
     * @param ids 支付订单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void removePayOrder(List<Long> ids) {
        // 删除支付订单
        this.payOrderService.removeBatchByIds(ids);
        // 删除支付记录
        this.payRecordService.removePayRecordByOrderIds(ids);
    }

    /**
     * 获取当前实例
     */
    public PayOrderBizService getSelf() {
        return SpringUtil.getBean(PayOrderBizService.class);
    }
}
