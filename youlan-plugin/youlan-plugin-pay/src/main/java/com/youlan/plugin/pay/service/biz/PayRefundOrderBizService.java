package com.youlan.plugin.pay.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.common.validator.helper.ValidatorHelper;
import com.youlan.plugin.pay.client.PayClient;
import com.youlan.plugin.pay.entity.PayChannel;
import com.youlan.plugin.pay.entity.PayConfig;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.PayRefundOrder;
import com.youlan.plugin.pay.entity.dto.CreateRefundOrderDTO;
import com.youlan.plugin.pay.entity.request.RefundQueryRequest;
import com.youlan.plugin.pay.entity.request.RefundRequest;
import com.youlan.plugin.pay.entity.response.RefundResponse;
import com.youlan.plugin.pay.entity.vo.CreateRefundOrderVO;
import com.youlan.plugin.pay.enums.NotifyType;
import com.youlan.plugin.pay.enums.RefundStatus;
import com.youlan.plugin.pay.enums.TradeType;
import com.youlan.plugin.pay.factory.PayClientFactory;
import com.youlan.plugin.pay.service.PayChannelService;
import com.youlan.plugin.pay.service.PayConfigService;
import com.youlan.plugin.pay.service.PayOrderService;
import com.youlan.plugin.pay.service.PayRefundOrderService;
import com.youlan.plugin.pay.utils.PayUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class PayRefundOrderBizService {
    private final PayOrderService payOrderService;
    private final PayRefundOrderService payRefundOrderService;
    private final PayClientFactory payClientFactory;
    private final PayConfigService payConfigService;
    private final PayChannelService payChannelService;
    private final PayNotifyBizService payNotifyBizService;

    /**
     * 根据商户退款单号获取退款订单
     *
     * @param mchRefundId 商户退款单号
     * @return 退款订单
     */
    public PayRefundOrder getRefundOrderByMchRefundId(String mchRefundId) {
        return payRefundOrderService.loadRefundOrderByMchRefundIdNotNull(mchRefundId);
    }

    /**
     * 根据退款订单ID获取退款订单
     *
     * @param orderId 退款订单ID
     * @return 退款订单
     */
    public PayRefundOrder getRefundOrderByOrderId(Long orderId) {
        return payRefundOrderService.loadRefundOrderNotNull(orderId);
    }

    /**
     * 创建退款订单
     *
     * @param dto 退款订单创建参数
     * @return 退款订单创建结果
     */
    public CreateRefundOrderVO createRefundOrder(CreateRefundOrderDTO dto) {
        // 获取可以退款的支付订单
        PayOrder payOrder = getPayOrderCanRefund(dto);
        // 获取可以使用的支付通道
        PayChannel payChannel = this.payChannelService.loadPayChannelEnabled(payOrder.getChannelId());
        // 获取可以使用的支付配置
        PayConfig payConfig = this.payConfigService.loadPayConfigEnabled(payOrder.getConfigId());
        // 获取支付客户端
        PayClient payClient = payClientFactory.createPayClient(payConfig, payOrder.getTradeType());
        // 生成外部退款订单号
        String outRefundNo = PayUtil.generateRefundOrderNo();
        // 创建退款订单
        PayRefundOrder payRefundOrder = new PayRefundOrder()
                .setMchOrderId(dto.getMchOrderId())
                .setMchRefundId(dto.getMchRefundId())
                .setRefundAmount(dto.getRefundAmount())
                .setRefundStatus(RefundStatus.WAITING)
                .setOrderId(payOrder.getId())
                .setOutTradeNo(payOrder.getOutTradeNo())
                .setTradeNo(payOrder.getTradeNo())
                .setTradeType(payOrder.getTradeType())
                .setOutRefundNo(outRefundNo)
                .setRefundReason(dto.getRefundReason())
                .setChannelId(payOrder.getChannelId())
                .setConfigId(payOrder.getConfigId())
                .setNotifyUrl(payChannel.getRefundNotifyUrl());
        // 保存退款订单
        payRefundOrderService.save(payRefundOrder);
        try {
            // 客户端发起退款请求
            RefundRequest refundRequest = new RefundRequest()
                    .setOutTradeNo(payOrder.getOutTradeNo())
                    .setOutRefundNo(outRefundNo)
                    .setRefundReason(dto.getRefundReason())
                    .setPayAmount(payOrder.getPayAmount())
                    .setRefundAmount(dto.getRefundAmount())
                    .setNotifyUrl(PayUtil.generateUnifiedRefundNotifyUrl(payChannel.getId(), payOrder.getTradeType()));
            RefundResponse refundResponse = payClient.refund(refundRequest);
            RefundStatus refundStatus = refundResponse.getRefundStatus();
            switch (refundStatus) {
                case WAITING:
                    // 待退款处理逻辑
                    getSelf().doRefundOrderWaiting(refundResponse);
                    break;
                case SUCCESS:
                    // 已退款处理逻辑
                    getSelf().doRefundOrderSuccess(refundResponse);
                    break;
                case FAILED:
                    // 已失败处理逻辑
                    getSelf().doRefundOrderFailed(refundResponse);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // 获取订单最新数据
        payRefundOrder = this.payRefundOrderService.loadRefundOrderNotNull(payRefundOrder.getId());
        return new CreateRefundOrderVO()
                .setOrderId(payRefundOrder.getId())
                .setRefundStatus(payRefundOrder.getRefundStatus());
    }

    /**
     * 调度退款订单同步
     */
    public void scheduleSyncRefundOrder() {
        List<PayRefundOrder> refundWaitingOrderList = payRefundOrderService.getRefundWaitingOrderList();
        if (CollectionUtil.isEmpty(refundWaitingOrderList)) {
            return;
        }
        int successCount = 0;
        for (PayRefundOrder payRefundOrder : refundWaitingOrderList) {
            successCount += syncRefundOrder(payRefundOrder) ? 1 : 0;
        }
        log.info("退款订单同步调度任务: {订单总数: {}, 已同步: {}}", refundWaitingOrderList.size(), successCount);
    }

    /**
     * 退款订单同步
     *
     * @param payRefundOrder 退款订单
     * @return 是否同步成功
     */
    public boolean syncRefundOrder(PayRefundOrder payRefundOrder) {
        String refundOrderSyncRedisKey = PayUtil.getRefundOrderSyncRedisKey(payRefundOrder.getId());
        RLock lock = RedisHelper.getLock(refundOrderSyncRedisKey);
        // 未获取到锁时说明订单正在被处理，此处需要跳过该订单
        try {
            boolean tryLock = lock.tryLock(0, 30, TimeUnit.SECONDS);
            if (!tryLock) {
                log.warn("退款订单已在处理中，此处忽略同步：{订单ID: {}}", payRefundOrder.getOrderId());
                return false;
            }
            TradeType tradeType = payRefundOrder.getTradeType();
            Long configId = payRefundOrder.getConfigId();
            PayConfig payConfig = payConfigService.loadPayConfigNotNull(configId);
            PayClient payClient = payClientFactory.createPayClient(payConfig, tradeType);
            RefundQueryRequest refundQueryRequest = new RefundQueryRequest()
                    .setOutRefundNo(payRefundOrder.getOutRefundNo())
                    .setOutTradeNo(payRefundOrder.getOutTradeNo());
            RefundResponse refundResponse = payClient.refundQuery(refundQueryRequest);
            RefundStatus refundStatus = refundResponse.getRefundStatus();
            switch (refundStatus) {
                case SUCCESS:
                    // 退款成功
                    getSelf().doRefundOrderSuccess(refundResponse);
                    break;
                case FAILED:
                    // 等待退款
                    getSelf().doRefundOrderFailed(refundResponse);
                    break;
                default:
                    break;
            }
            return true;
        } catch (InterruptedException e) {
            log.error("退款订单同步失败：{id: {}}", payRefundOrder.getId());
            log.error(e.getMessage(), e);
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取可以退款的支付订单
     *
     * @param dto 退款订单创建参数
     * @return 支付订单
     */
    private PayOrder getPayOrderCanRefund(CreateRefundOrderDTO dto) {
        // 参数校验
        ValidatorHelper.validateWithThrow(dto);
        // 获取商户订单号
        String mchOrderId = dto.getMchOrderId();
        // 商户退款号不能重复
        boolean existsMchRefundId = payRefundOrderService.existsByMchRefundId(dto.getMchRefundId());
        if (existsMchRefundId) {
            throw new BizRuntimeException(ApiResultCode.E0031);
        }
        // 获取支付订单
        PayOrder payOrder = payOrderService.loadPayOrderCanRefundByMchOrderId(mchOrderId, dto.getRefundAmount());
        // 不能同时存在多条待退款的支付订单，否则可能会出现发起多次退款后不够退的情况
        boolean existsRefundWaiting = payRefundOrderService.existsRefundWaitingByOrderId(payOrder.getId());
        if (existsRefundWaiting) {
            throw new BizRuntimeException(ApiResultCode.E0027);
        }
        return payOrder;
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
        log.info("退款回调: {configId: {}, tradeType: {}, params: {}, body: {}}", configId, tradeType,
                JSONUtil.toJsonStr(params), body);
        PayConfig payConfig = payConfigService.loadPayConfigNotNull(configId);
        PayClient payClient = payClientFactory.createPayClient(payConfig, tradeType);
        RefundResponse refundResponse = payClient.refundNotifyParse(params, body);
        RefundStatus refundStatus = refundResponse.getRefundStatus();
        switch (refundStatus) {
            case SUCCESS:
                // 已成功处理逻辑
                getSelf().doRefundOrderSuccess(refundResponse);
                break;
            case FAILED:
                // 已失败处理逻辑
                getSelf().doRefundOrderFailed(refundResponse);
                break;
            default:
                log.info("退款回调不支持此状态: {configId: {}, tradeType: {}, params: {}, body: {}}", configId, tradeType,
                        JSONUtil.toJsonStr(params), body);
                break;
        }
    }

    /**
     * 更新退款订单为待退款状态
     *
     * @param refundResponse 退款响应
     */
    @Transactional(rollbackFor = Exception.class)
    public void doRefundOrderWaiting(RefundResponse refundResponse) {
        // 获取退款订单
        PayRefundOrder payRefundOrder = payRefundOrderService.loadRefundOrderByOutRefundNoNotNull(refundResponse.getOutRefundNo());
        RefundStatus refundStatus = payRefundOrder.getRefundStatus();
        switch (refundStatus) {
            case FAILED:
                log.info("退款订单是已失败状态，无法更新：{id: {}}", payRefundOrder.getId());
                break;
            case SUCCESS:
                log.info("退款订单是已退款状态，无法更新：{id: {}}", payRefundOrder.getId());
                break;
            case WAITING:
                // 创建更新退款订单
                PayRefundOrder updatePayRefundOrder = new PayRefundOrder()
                        .setId(payRefundOrder.getId())
                        .setRefundStatus(RefundStatus.WAITING)
                        .setRefundNo(refundResponse.getRefundNo());
                // 根据响应来源设置原始数据
                if (refundResponse.getResponseSource().isRefundNotify()) {
                    updatePayRefundOrder.setNotifyRawData(refundResponse);
                } else {
                    updatePayRefundOrder.setRawData(refundResponse);
                }
                payRefundOrderService.updateByIdAndRefundStatus(payRefundOrder.getId(), RefundStatus.WAITING, updatePayRefundOrder);
                break;
            default:
                throw new BizRuntimeException(ApiResultCode.E0030);
        }
    }

    /**
     * 更新退款订单为已退款状态
     *
     * @param refundResponse 退款响应
     */
    @Transactional(rollbackFor = Exception.class)
    public void doRefundOrderSuccess(RefundResponse refundResponse) {
        // 获取退款订单
        PayRefundOrder payRefundOrder = payRefundOrderService.loadRefundOrderByOutRefundNoNotNull(refundResponse.getOutRefundNo());
        RefundStatus refundStatus = payRefundOrder.getRefundStatus();
        // 待退款需要更新退款订单，已成功不需要，其他状态中断执行
        switch (refundStatus) {
            case SUCCESS:
                log.info("退款订单是已退款状态，无需更新：{id: {}}", payRefundOrder.getId());
                break;
            case WAITING:
                // 创建更新退款订单
                PayRefundOrder updatePayRefundOrder = new PayRefundOrder()
                        .setId(payRefundOrder.getId())
                        .setRefundStatus(RefundStatus.SUCCESS)
                        .setRefundNo(refundResponse.getRefundNo())
                        .setSuccessTime(refundResponse.getSuccessTime());
                // 根据响应来源设置原始数据
                if (refundResponse.getResponseSource().isRefundNotify()) {
                    updatePayRefundOrder.setNotifyRawData(refundResponse);
                } else {
                    updatePayRefundOrder.setRawData(refundResponse);
                }
                boolean update = payRefundOrderService.updateByIdAndRefundStatus(payRefundOrder.getId(), RefundStatus.WAITING, updatePayRefundOrder);
                if (!update) {
                    throw new BizRuntimeException(ApiResultCode.E0030);
                }
                // 更新支付订单退款金额
                payOrderService.updatePayOrderRefundAmount(payRefundOrder.getOrderId(), payRefundOrder.getRefundAmount());
                // 创建退款回调通知
                payNotifyBizService.createPayNotify(NotifyType.REFUND, payRefundOrder.getId(), payRefundOrder.getNotifyUrl());
                break;
            default:
                throw new BizRuntimeException(ApiResultCode.E0030);
        }
    }

    /**
     * 更新退款订单为已失败状态
     *
     * @param refundResponse 退款响应
     */
    @Transactional(rollbackFor = Exception.class)
    public void doRefundOrderFailed(RefundResponse refundResponse) {
        // 获取退款订单ID
        PayRefundOrder payRefundOrder = payRefundOrderService.loadRefundOrderByOutRefundNoNotNull(refundResponse.getOutRefundNo());
        RefundStatus refundStatus = payRefundOrder.getRefundStatus();
        // 待退款需要更新退款订单，已失败需要处理，其他状态中断执行
        switch (refundStatus) {
            case FAILED:
                log.info("退款订单是已失败状态，无需更新：{id: {}}", payRefundOrder.getId());
                break;
            case WAITING:
                // 创建更新退款订单
                PayRefundOrder updatePayRefundOrder = new PayRefundOrder()
                        .setId(payRefundOrder.getId())
                        .setRefundStatus(RefundStatus.FAILED)
                        .setRefundNo(refundResponse.getRefundNo())
                        .setErrorCode(refundResponse.getErrorCode())
                        .setErrorMsg(refundResponse.getErrorMsg());
                if (refundResponse.getResponseSource().isRefundNotify()) {
                    updatePayRefundOrder.setNotifyRawData(refundResponse);
                } else {
                    updatePayRefundOrder.setRawData(refundResponse);
                }
                boolean update = payRefundOrderService.updateByIdAndRefundStatus(payRefundOrder.getId(), RefundStatus.WAITING, updatePayRefundOrder);
                if (!update) {
                    throw new BizRuntimeException(ApiResultCode.E0030);
                }
                // 创建退款回调通知
                payNotifyBizService.createPayNotify(NotifyType.REFUND, payRefundOrder.getId(), payRefundOrder.getNotifyUrl());
                break;
            default:
                throw new BizRuntimeException(ApiResultCode.E0030);
        }
    }

    /**
     * 获取自身实例
     */
    public PayRefundOrderBizService getSelf() {
        return SpringUtil.getBean(PayRefundOrderBizService.class);
    }
}
