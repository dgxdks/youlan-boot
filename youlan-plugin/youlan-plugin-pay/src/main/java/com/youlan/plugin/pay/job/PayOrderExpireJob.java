package com.youlan.plugin.pay.job;

import com.youlan.plugin.pay.service.biz.PayOrderBizService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "youlan.pay", name = {"order-expire-enabled"}, havingValue = "true")
public class PayOrderExpireJob {
    private PayOrderBizService payOrderBizService;

    @Scheduled(cron = "${youlan.pay.order-expire-cron}")
    public void scheduleSyncPayOrder() {
        try {
            payOrderBizService.scheduleExpirePayOrder();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
