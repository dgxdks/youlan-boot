package com.youlan.system.runner;

import com.youlan.system.service.biz.ConfigBizService;
import com.youlan.system.service.biz.DictBizService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SystemApplicationRunner implements ApplicationRunner {
    private final DictBizService dictBizService;
    private final ConfigBizService configBizService;

    @Override
    public void run(ApplicationArguments args) {
        dictBizService.setDictCache();
        configBizService.setConfigCache();
    }
}
