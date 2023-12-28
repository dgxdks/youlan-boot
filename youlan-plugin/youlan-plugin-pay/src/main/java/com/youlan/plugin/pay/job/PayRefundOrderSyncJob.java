package com.youlan.plugin.pay.job;

import com.youlan.plugin.pay.service.biz.PayRefundOrderBizService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "youlan.pay", name = {"refund-order-sync-enabled"}, havingValue = "true")
public class PayRefundOrderSyncJob {

    private final PayRefundOrderBizService payRefundOrderBizService;

    @Scheduled(cron = "${youlan.pay.refund-order-sync-cron}")
    public void scheduleSyncRefundOrder() {
        try {
            payRefundOrderBizService.scheduleSyncRefundOrder();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
