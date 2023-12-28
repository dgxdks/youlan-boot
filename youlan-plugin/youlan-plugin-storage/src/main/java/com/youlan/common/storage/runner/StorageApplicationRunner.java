package com.youlan.common.storage.runner;

import com.youlan.common.storage.service.StorageConfigService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StorageApplicationRunner implements ApplicationRunner {

    private final StorageConfigService storageConfigService;

    @Override
    public void run(ApplicationArguments args) {
        storageConfigService.refreshStorageConfigCache();
    }
}
