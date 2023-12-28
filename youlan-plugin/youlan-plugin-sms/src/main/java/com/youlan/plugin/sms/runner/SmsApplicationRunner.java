package com.youlan.plugin.sms.runner;

import com.youlan.plugin.sms.service.SmsSupplierService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SmsApplicationRunner implements ApplicationRunner {
    private final SmsSupplierService smsSupplierService;

    @Override
    public void run(ApplicationArguments args) {
        smsSupplierService.refreshSmsSupplierCache();
    }
}
