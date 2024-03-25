package com.youlan.plugin.pay.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.jackson.helper.JacksonHelper;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.redis.helper.RedisHelper;
import com.youlan.plugin.pay.config.PayProperties;
import com.youlan.plugin.pay.constant.PayConstant;
import com.youlan.plugin.pay.entity.PayNotify;
import com.youlan.plugin.pay.entity.PayNotifyRecord;
import com.youlan.plugin.pay.entity.PayOrder;
import com.youlan.plugin.pay.entity.PayRefundOrder;
import com.youlan.plugin.pay.entity.dto.ChannelPayNotifyDTO;
import com.youlan.plugin.pay.entity.dto.ChannelRefundNotifyDTO;
import com.youlan.plugin.pay.enums.NotifyStatus;
import com.youlan.plugin.pay.enums.NotifyType;
import com.youlan.plugin.pay.service.PayNotifyRecordService;
import com.youlan.plugin.pay.service.PayNotifyService;
import com.youlan.plugin.pay.service.PayOrderService;
import com.youlan.plugin.pay.service.PayRefundOrderService;
import com.youlan.plugin.pay.utils.PayUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import static com.youlan.plugin.pay.constant.PayConstant.PYA_NOTIFY_THREAD_POOL_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class PayNotifyBizService {
    @Resource(name = PYA_NOTIFY_THREAD_POOL_NAME)
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final PayProperties payProperties;
    private final PayNotifyService payNotifyService;
    private final PayNotifyRecordService payNotifyRecordService;
    private final PayOrderService payOrderService;
    private final PayRefundOrderService payRefundOrderService;

    /**
     * 创建支付回调
     *
     * @param notifyType 回调类型
     * @param orderId    订单ID(支付回调时-支付订单ID 退款回调时-退款订单ID)
     * @param notifyUrl  回调地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void createPayNotify(NotifyType notifyType, Long orderId, String notifyUrl) {
        Assert.notNull(notifyType, "回调类型不能为空");
        Assert.notNull(orderId, "订单ID不能为空");
        Assert.notNull(notifyUrl, "回调地址不能为空");
        PayNotify payNotify = new PayNotify()
                .setNotifyType(notifyType)
                .setNotifyStatus(NotifyStatus.NOTIFY_WAITING)
                .setOrderId(orderId)
                .setNotifyUrl(notifyUrl)
                .setNotifyTimes(0)
                .setNextNotifyTime(new Date());
        // 保存支付回调
        payNotifyService.save(payNotify);
        // 事务提交后执行支付回调
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 调度支付回调
                schedulePayNotify(payNotify);
            }
        });
    }

    /**
     * 调度支付回调
     */
    public void schedulePayNotify() {
        // 获取可以回调的支付回调列表
        List<PayNotify> payNotifyList = this.payNotifyService.getPayNotifyListCanNotify();
        if (CollectionUtil.isEmpty(payNotifyList)) {
            return;
        }
        log.info("支付回调调度任务：{回调总数: {}}", payNotifyList.size());
        payNotifyList.forEach(payNotify -> threadPoolTaskExecutor.execute(() -> {
            try {
                // 执行支付回调
                schedulePayNotify(payNotify);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }));
    }

    /**
     * 调度支付回调
     *
     * @param payNotify 支付回调
     */
    public void schedulePayNotify(PayNotify payNotify) {
        String payNotifyRedisKey = PayUtil.getPayNotifyRedisKey(payNotify.getId());
        RedisHelper.underLock(payNotifyRedisKey, () -> {
            PayNotify newPayNotify = payNotifyService.loadPayNotifyNotNull(payNotify.getId());
            // 加锁不能完全保证同一条支付回调只会被执行一次，需要通过查询库里最新的回调次数，只有前后一致才能保证此支付回调不会被重复执行
            if (ObjectUtil.notEqual(payNotify.getNotifyTimes(), newPayNotify.getNotifyTimes())) {
                log.warn("支付回调次数与实际不一致被忽略，可能存在锁竞争: {支付回调: {}, 实际回调次数: {}}", JSONUtil.toJsonStr(payNotify),
                        newPayNotify.getNotifyTimes());
                return;
            }
            // 执行支付回调
            getSelf().doPayNotify(payNotify);
        }, Duration.ofSeconds(payProperties.getNotifyTimeout()));
    }

    /**
     * 执行支付回调
     *
     * @param payNotify 支付回调
     */
    @Transactional(rollbackFor = Exception.class)
    public void doPayNotify(PayNotify payNotify) {
        NotifyType notifyType = payNotify.getNotifyType();
        Object notifyDTO;
        switch (notifyType) {
            case PAY:
                PayOrder payOrder = payOrderService.loadPayOrderNotNull(payNotify.getOrderId());
                notifyDTO = new ChannelPayNotifyDTO()
                        .setOrderId(payOrder.getId())
                        .setMchOrderId(payOrder.getMchOrderId())
                        .setPayStatus(payOrder.getPayStatus());
                break;
            case REFUND:
                PayRefundOrder payRefundOrder = payRefundOrderService.loadRefundOrderNotNull(payNotify.getOrderId());
                notifyDTO = new ChannelRefundNotifyDTO()
                        .setRefundId(payRefundOrder.getId())
                        .setOrderId(payRefundOrder.getOrderId())
                        .setMchOrderId(payRefundOrder.getMchOrderId())
                        .setMchRefundId(payRefundOrder.getMchRefundId())
                        .setRefundStatus(payRefundOrder.getRefundStatus());
                break;
            default:
                throw new BizRuntimeException("回调类型不支持");
        }
        HttpResponse httpResponse = null;
        ApiResult apiResult = null;
        Exception notifyException = null;
        // 响应体
        String responseBody = null;
        // 创建请求体,此处必须使用jackson序列化
        String requestBody = JacksonHelper.toJsonStr(notifyDTO);
        try {
            // 发起支付回调请求
            httpResponse = HttpUtil.createPost(payNotify.getNotifyUrl())
                    .body(requestBody)
                    .timeout(payProperties.getNotifyTimeout() * 1000)
                    .execute();
            responseBody = httpResponse.body();
            apiResult = JSONUtil.toBean(responseBody, ApiResult.class);
        } catch (Exception e) {
            notifyException = e;
        } finally {
            IoUtil.close(httpResponse);
        }
        // 计算当前回调次数
        int notifyTimes = payNotify.getNotifyTimes() + 1;
        NotifyStatus notifyStatus = this.updateAndGetPayNotifyStatus(payNotify.getId(), notifyTimes, apiResult, notifyException);
        // 创建支付回调记录
        PayNotifyRecord payNotifyRecord = new PayNotifyRecord()
                .setNotifyId(payNotify.getId())
                .setNotifyStatus(notifyStatus)
                .setNotifyTimes(notifyTimes)
                .setRequestBody(requestBody)
                .setResponseBody(ObjectUtil.isNull(apiResult) ? responseBody : apiResult.toString())
                .setErrorMsg(ObjectUtil.isNull(notifyException) ? null : ExceptionUtil.getRootCauseMessage(notifyException));
        // 保存支付回调记录
        this.payNotifyRecordService.save(payNotifyRecord);
    }


    /**
     * 更新并获取支付回调状态
     *
     * @param notifyId        支付回调ID
     * @param notifyTimes     支付回调次数
     * @param notifyApiResult 支付回调响应结果
     * @param notifyException 支付回调异常
     * @return 支付回调状态
     */
    public NotifyStatus updateAndGetPayNotifyStatus(Long notifyId, Integer notifyTimes, ApiResult notifyApiResult, Exception notifyException) {
        Date now = new Date();
        PayNotify payNotify = new PayNotify()
                .setId(notifyId)
                .setNotifyTimes(notifyTimes)
                .setLastNotifyTime(now);
        // 如果支付回调成功
        if (ObjectUtil.isNotNull(notifyApiResult) && notifyApiResult.isOk()) {
            payNotify.setNotifyStatus(NotifyStatus.NOTIFY_SUCCESS);
            this.payNotifyService.updateById(payNotify);
            return NotifyStatus.NOTIFY_SUCCESS;
        }
        // 如果超过最大重试次数
        if (notifyTimes >= PayConstant.NOTIFY_FREQUENCY.length) {
            payNotify.setNotifyStatus(NotifyStatus.NOTIFY_FAILED);
            this.payNotifyService.updateById(payNotify);
            return NotifyStatus.NOTIFY_FAILED;
        }
        // 如果没有超过最大重试次数
        // 使用上一次的回调次数计算下次回调时间
        Date nextNotifyTime = DateUtil.offsetSecond(now, PayConstant.NOTIFY_FREQUENCY[payNotify.getNotifyTimes()]);
        NotifyStatus notifyStatus = ObjectUtil.isNull(notifyException) ? NotifyStatus.REQUEST_SUCCESS : NotifyStatus.REQUEST_FAILED;
        payNotify.setNextNotifyTime(nextNotifyTime)
                .setNotifyStatus(notifyStatus);
        this.payNotifyService.updateById(payNotify);
        return notifyStatus;
    }

    /**
     * 获取自身
     */
    public PayNotifyBizService getSelf() {
        return SpringUtil.getBean(PayNotifyBizService.class);
    }
}
