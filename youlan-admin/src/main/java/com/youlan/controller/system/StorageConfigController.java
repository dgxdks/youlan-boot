package com.youlan.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.enums.ApiResultCode;
import com.youlan.common.db.entity.dto.ListDTO;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.framework.anno.OperationLog;
import com.youlan.framework.constant.OperationLogType;
import com.youlan.framework.controller.BaseController;
import com.youlan.system.entity.StorageConfig;
import com.youlan.system.service.StorageConfigService;
import com.youlan.system.service.biz.StorageBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "存储管理")
@RestController
@RequestMapping("/system/storageConfig")
@AllArgsConstructor
public class StorageConfigController extends BaseController {
    private final StorageBizService storageBizService;
    private final StorageConfigService storageConfigService;

    @SaCheckPermission("system:storageConfig:add")
    @Operation(summary = "存储配置新增")
    @OperationLog(name = "存储配置", type = OperationLogType.OPERATION_LOG_TYPE_ADD)
    @PostMapping("/addStorageConfig")
    public ApiResult addStorageConfig(@Validated @RequestBody StorageConfig storageConfig) {
        storageBizService.addStorageConfig(storageConfig);
        return toSuccess();
    }

    @SaCheckPermission("system:storageConfig:update")
    @Operation(summary = "存储配置修改")
    @PostMapping("/updateStorageConfig")
    @OperationLog(name = "存储配置", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateStorageConfig(@Validated @RequestBody StorageConfig storageConfig) {
        if (ObjectUtil.isNull(storageConfig.getId())) {
            return toError(ApiResultCode.C0001);
        }
        storageBizService.updateStorageConfig(storageConfig);
        return toSuccess();
    }

    @SaCheckPermission("system:storageConfig:remove")
    @Operation(summary = "存储配置删除")
    @PostMapping("/removeStorageConfig")
    @OperationLog(name = "存储配置", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult removeStorageConfig(@Validated @RequestBody ListDTO<Long> dto) {
        if (CollectionUtil.isEmpty(dto.getList())) {
            return toSuccess();
        }
        storageBizService.removeStorageConfigs(dto.getList());
        return toSuccess();
    }

    @SaCheckPermission("system:storageConfig:load")
    @Operation(summary = "存储配置详情")
    @PostMapping("/loadStorageConfig")
    public ApiResult loadStorageConfig(@RequestParam Long id) {
        return toSuccess(storageConfigService.loadOne(id));
    }

    @SaCheckPermission("system:storageConfig:list")
    @Operation(summary = "存储配置分页")
    @PostMapping("/getStorageConfigPageList")
    @OperationLog(name = "存储配置", type = OperationLogType.OPERATION_LOG_TYPE_PAGE_LIST)
    public ApiResult getStorageConfigPageList(@RequestBody StorageConfig storageConfig) {
        return toSuccess(storageConfigService.loadPage(storageConfig, DBHelper.getQueryWrapper(storageConfig)));
    }

    @SaCheckPermission("system:storageConfig:export")
    @Operation(summary = "存储配置导出")
    @PostMapping("/exportStorageConfigList")
    @OperationLog(name = "存储配置", type = OperationLogType.OPERATION_LOG_TYPE_EXPORT)
    public void exportStorageConfigList(@RequestBody StorageConfig storageConfig, HttpServletResponse response) throws IOException {
        List<StorageConfig> storageConfigs = storageConfigService.loadMore(DBHelper.getQueryWrapper(storageConfig));
        toExcel("存储配置.xlsx", "存储配置", StorageConfig.class, storageConfigs, response);
    }

    @SaCheckPermission("system:storageConfig:update")
    @Operation(summary = "存储配置状态修改")
    @PostMapping("/updateStorageConfigStatus")
    @OperationLog(name = "存储配置", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateStorageConfigStatus(@RequestParam Long id, @RequestParam String status) {
        storageBizService.updateStorageConfigStatus(id, status);
        return toSuccess();
    }

    @SaCheckPermission("system:storageConfig:update")
    @Operation(summary = "存储配置是否默认")
    @PostMapping("/updateStorageConfigIsDefault")
    @OperationLog(name = "存储配置", type = OperationLogType.OPERATION_LOG_TYPE_UPDATE)
    public ApiResult updateStorageConfigIsDefault(@RequestParam Long id, @RequestParam String isDefault) {
        storageBizService.updateStorageConfigIsDefault(id, isDefault);
        return toSuccess();
    }

    @SaCheckPermission("system:storageConfig:remove")
    @Operation(summary = "存储配置缓存刷新")
    @PostMapping("/refreshStorageConfigCache")
    @OperationLog(name = "存储配置", type = OperationLogType.OPERATION_LOG_TYPE_REMOVE)
    public ApiResult refreshStorageConfigCache() {
        storageBizService.refreshStorageConfigCache();
        return toSuccess();
    }
}
