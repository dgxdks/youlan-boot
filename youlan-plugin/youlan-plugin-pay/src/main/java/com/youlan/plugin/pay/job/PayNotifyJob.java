package com.youlan.plugin.pay.job;

import com.youlan.plugin.pay.service.biz.PayNotifyBizService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(prefix = "youlan.pay", name = {"notify-enabled"}, havingValue = "true")
public class PayNotifyJob {

    private final PayNotifyBizService payNotifyBizService;

    @Scheduled(cron = "${youlan.pay.notify-cron}")
    public void schedulePayNotify() {
        try {
            payNotifyBizService.schedulePayNotify();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
