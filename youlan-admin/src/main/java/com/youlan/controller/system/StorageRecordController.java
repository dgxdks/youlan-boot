package com.youlan.controller.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "存储管理")
@RestController
@RequestMapping("/system/storageRecord")
@AllArgsConstructor
public class StorageRecordController {
}
